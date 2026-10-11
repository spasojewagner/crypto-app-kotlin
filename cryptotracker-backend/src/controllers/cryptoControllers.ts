import mongoose from 'mongoose';
import { Request, Response } from 'express';
import { 
  getAllCryptoPrices, 
  getCryptoPrice, 
  getCryptoChartData,
  checkBinanceHealth 
} from '../services/binanceService';
import Portfolio from '../models/Portfolio';

/**
 * GET /api/crypto/prices
 * Dobavi sve crypto cene
 */
export const getAllPrices = async (req: Request, res: Response): Promise<void> => {
  try {
    const prices = await getAllCryptoPrices();
    
    res.status(200).json({
      success: true,
      data: prices,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('❌ Error fetching prices:', error);
    res.status(500).json({
      success: false,
      message: 'Greška pri dobavljanju cena'
    });
  }
};

/**
 * GET /api/crypto/price/:symbol
 * Dobavi cenu za jedan kripto
 */
export const getSinglePrice = async (req: Request, res: Response): Promise<void> => {
  try {
    const symbol = String(req.params.symbol);
    const price = await getCryptoPrice(symbol);
    
    if (!price) {
      res.status(404).json({
        success: false,
        message: 'Kriptovaluta nije pronađena'
      });
      return;
    }

    res.status(200).json({
      success: true,
      data: price
    });
  } catch (error) {
    console.error('❌ Error fetching single price:', error);
    res.status(500).json({
      success: false,
      message: 'Greška pri dobavljanju cene'
    });
  }
};

/**
 * GET /api/crypto/chart/:symbol
 * Dobavi podatke za grafikon
 */
export const getChartData = async (req: Request, res: Response): Promise<void> => {
  try {
    const symbol = String(req.params.symbol);
    const { interval = '1h', limit = 24 } = req.query;
    
    const chartData = await getCryptoChartData(
      symbol, 
      interval as string, 
      parseInt(limit as string)
    );

    res.status(200).json({
      success: true,
      data: chartData
    });
  } catch (error) {
    console.error('❌ Error fetching chart data:', error);
    res.status(500).json({
      success: false,
      message: 'Greška pri dobavljanju grafikona'
    });
  }
};

/**
 * GET /api/crypto/portfolio/:userId
 * Dobavi portfolio korisnika sa trenutnim cenama
 */
export const getUserPortfolio = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId = String(req.params.userId);
    if (!mongoose.isValidObjectId(userId)) {
      res.status(400).json({ success: false, message: 'Neispravan korisnik' });
      return;
    }
    
    let portfolio = await Portfolio.findOne({ userId });
    
    // Ako portfolio ne postoji, kreiraj prazan
    if (!portfolio) {
      portfolio = new Portfolio({
        userId,
        items: [],
        totalBalanceUSD: 0
      });
      await portfolio.save();
    }

    // Dobavi trenutne cene za sve crypto u portfoliju
    const prices = await getAllCryptoPrices();
    
    // Izračunaj ukupnu vrednost portfolija
    let totalBalance = 0;
    const portfolioWithPrices = portfolio.items.map(item => {
      const currentPrice = prices.find(p => p.symbol === item.symbol);
      const currentValue = item.amount * (currentPrice?.price || 0);
      const profitLoss = currentValue - (item.amount * item.averageBuyPrice);
      const profitLossPercent = ((currentValue - (item.amount * item.averageBuyPrice)) / (item.amount * item.averageBuyPrice)) * 100;

      totalBalance += currentValue;

      return {
        ...(item as unknown as { toObject: () => Record<string, unknown> }).toObject(),
        currentPrice: currentPrice?.price || 0,
        currentValue: currentValue,
        profitLoss: profitLoss,
        profitLossPercent: profitLossPercent,
        priceChange24h: currentPrice?.priceChangePercent24h || 0
      };
    });

    res.status(200).json({
      success: true,
      data: {
        portfolio: portfolioWithPrices,
        totalBalanceUSD: totalBalance,
        lastUpdated: portfolio.lastUpdated
      }
    });
  } catch (error) {
    console.error('❌ Error fetching portfolio:', error);
    res.status(500).json({
      success: false,
      message: 'Greška pri dobavljanju portfolija'
    });
  }
};

/**
 * POST /api/crypto/portfolio/add
 * Dodaj crypto u portfolio
 */
export const addToPortfolio = async (req: Request, res: Response): Promise<void> => {
  try {
    // Plain strings and positive numbers only: an object here would be a MongoDB query operator.
    const userId = typeof req.body?.userId === 'string' ? req.body.userId : '';
    const symbol = typeof req.body?.symbol === 'string' ? req.body.symbol : '';
    const name = typeof req.body?.name === 'string' ? req.body.name : '';
    const amount = Number(req.body?.amount);
    const buyPrice = Number(req.body?.buyPrice);

    if (!mongoose.isValidObjectId(userId) || !symbol || !name || !(amount > 0) || !(buyPrice > 0)) {
      res.status(400).json({
        success: false,
        message: 'Sva polja su obavezna'
      });
      return;
    }

    let portfolio = await Portfolio.findOne({ userId });

    if (!portfolio) {
      portfolio = new Portfolio({ userId, items: [] });
    }

    // Proveri da li već postoji u portfoliju
    const existingIndex = portfolio.items.findIndex(item => item.symbol === symbol);

    if (existingIndex !== -1) {
      // Update postojećeg
      const existing = portfolio.items[existingIndex]!;
      const totalAmount = existing.amount + amount;
      const totalCost = (existing.amount * existing.averageBuyPrice) + (amount * buyPrice);

      existing.amount = totalAmount;
      existing.averageBuyPrice = totalCost / totalAmount;
    } else {
      // Dodaj novi
      portfolio.items.push({
        symbol,
        name,
        amount,
        averageBuyPrice: buyPrice,
        isFavorite: false
      });
    }

    portfolio.lastUpdated = new Date();
    await portfolio.save();

    res.status(200).json({
      success: true,
      message: 'Crypto uspešno dodat u portfolio',
      data: portfolio
    });
  } catch (error) {
    console.error('❌ Error adding to portfolio:', error);
    res.status(500).json({
      success: false,
      message: 'Greška pri dodavanju u portfolio'
    });
  }
};

/**
 * PUT /api/crypto/portfolio/favorite
 * Toggle favorite status
 */
export const toggleFavorite = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId = typeof req.body?.userId === 'string' ? req.body.userId : '';
    const symbol = typeof req.body?.symbol === 'string' ? req.body.symbol : '';
    if (!mongoose.isValidObjectId(userId) || !symbol) {
      res.status(400).json({ success: false, message: 'Neispravni podaci' });
      return;
    }

    const portfolio = await Portfolio.findOne({ userId });
    
    if (!portfolio) {
      res.status(404).json({
        success: false,
        message: 'Portfolio nije pronađen'
      });
      return;
    }

    const item = portfolio.items.find(i => i.symbol === symbol);
    if (item) {
      item.isFavorite = !item.isFavorite;
      await portfolio.save();
    }

    res.status(200).json({
      success: true,
      data: portfolio
    });
  } catch (error) {
    console.error('❌ Error toggling favorite:', error);
    res.status(500).json({
      success: false,
      message: 'Greška'
    });
  }
};

/**
 * GET /api/crypto/health
 * Health check za Binance API
 */
export const healthCheck = async (req: Request, res: Response): Promise<void> => {
  try {
    const isHealthy = await checkBinanceHealth();
    
    res.status(isHealthy ? 200 : 503).json({
      success: isHealthy,
      message: isHealthy ? 'Binance API radi' : 'Binance API nedostupan',
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(503).json({
      success: false,
      message: 'Health check failed'
    });
  }
};