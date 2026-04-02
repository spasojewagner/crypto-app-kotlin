import axios from 'axios';

const BINANCE_API_URL = 'https://api.binance.com/api/v3';

export interface CryptoPrice {
  symbol: string;
  name: string;
  price: number;
  priceChange24h: number;
  priceChangePercent24h: number;
  volume24h: number;
  marketCap?: number;
  iconUrl: string;
}

// PROŠIRENA LISTA - 30+ kriptovaluta
const CRYPTO_SYMBOLS = [
  // Top 10
  { symbol: 'BTCUSDT', name: 'Bitcoin', ticker: 'BTC', icon: '₿' },
  { symbol: 'ETHUSDT', name: 'Ethereum', ticker: 'ETH', icon: 'Ξ' },
  { symbol: 'BNBUSDT', name: 'BNB', ticker: 'BNB', icon: 'BNB' },
  { symbol: 'SOLUSDT', name: 'Solana', ticker: 'SOL', icon: 'SOL' },
  { symbol: 'XRPUSDT', name: 'Ripple', ticker: 'XRP', icon: 'XRP' },
  { symbol: 'ADAUSDT', name: 'Cardano', ticker: 'ADA', icon: 'ADA' },
  { symbol: 'DOGEUSDT', name: 'Dogecoin', ticker: 'DOGE', icon: 'Ð' },
  { symbol: 'DOTUSDT', name: 'Polkadot', ticker: 'DOT', icon: 'DOT' },
  { symbol: 'MATICUSDT', name: 'Polygon', ticker: 'MATIC', icon: 'MATIC' },
  { symbol: 'AVAXUSDT', name: 'Avalanche', ticker: 'AVAX', icon: 'AVAX' },
  
  // 11-20
  { symbol: 'LINKUSDT', name: 'Chainlink', ticker: 'LINK', icon: 'LINK' },
  { symbol: 'UNIUSDT', name: 'Uniswap', ticker: 'UNI', icon: 'UNI' },
  { symbol: 'LTCUSDT', name: 'Litecoin', ticker: 'LTC', icon: 'Ł' },
  { symbol: 'ATOMUSDT', name: 'Cosmos', ticker: 'ATOM', icon: 'ATOM' },
  { symbol: 'ETCUSDT', name: 'Ethereum Classic', ticker: 'ETC', icon: 'ETC' },
  { symbol: 'XLMUSDT', name: 'Stellar', ticker: 'XLM', icon: 'XLM' },
  { symbol: 'ALGOUSDT', name: 'Algorand', ticker: 'ALGO', icon: 'ALGO' },
  { symbol: 'VETUSDT', name: 'VeChain', ticker: 'VET', icon: 'VET' },
  { symbol: 'ICPUSDT', name: 'Internet Computer', ticker: 'ICP', icon: 'ICP' },
  { symbol: 'FILUSDT', name: 'Filecoin', ticker: 'FIL', icon: 'FIL' },
  
  // 21-30
  { symbol: 'TRXUSDT', name: 'TRON', ticker: 'TRX', icon: 'TRX' },
  { symbol: 'APTUSDT', name: 'Aptos', ticker: 'APT', icon: 'APT' },
  { symbol: 'ARBUSDT', name: 'Arbitrum', ticker: 'ARB', icon: 'ARB' },
  { symbol: 'NEARUSDT', name: 'NEAR Protocol', ticker: 'NEAR', icon: 'NEAR' },
  { symbol: 'OPUSDT', name: 'Optimism', ticker: 'OP', icon: 'OP' },
  { symbol: 'INJUSDT', name: 'Injective', ticker: 'INJ', icon: 'INJ' },
  { symbol: 'SUIUSDT', name: 'Sui', ticker: 'SUI', icon: 'SUI' },
  { symbol: 'PEPEUSDT', name: 'Pepe', ticker: 'PEPE', icon: 'PEPE' },
  { symbol: 'SHIBUSDT', name: 'Shiba Inu', ticker: 'SHIB', icon: 'SHIB' },
  { symbol: 'LDOUSDT', name: 'Lido DAO', ticker: 'LDO', icon: 'LDO' },
  
  // Bonus (31-35)
  { symbol: 'AAVEUSDT', name: 'Aave', ticker: 'AAVE', icon: 'AAVE' },
  { symbol: 'GMXUSDT', name: 'GMX', ticker: 'GMX', icon: 'GMX' },
  { symbol: 'MKRUSDT', name: 'Maker', ticker: 'MKR', icon: 'MKR' },
  { symbol: 'GRTUSDT', name: 'The Graph', ticker: 'GRT', icon: 'GRT' },
  { symbol: 'SANDUSDT', name: 'The Sandbox', ticker: 'SAND', icon: 'SAND' }
];

/**
 * Dobavi trenutne cene za sve glavne kriptovalute
 */
export async function getAllCryptoPrices(): Promise<CryptoPrice[]> {
  try {
    // Dobavi 24h ticker za sve simbole
    const response = await axios.get(`${BINANCE_API_URL}/ticker/24hr`);
    const tickers = response.data;

    const cryptoPrices: CryptoPrice[] = [];

    for (const crypto of CRYPTO_SYMBOLS) {
      const ticker = tickers.find((t: any) => t.symbol === crypto.symbol);
      
      if (ticker) {
        cryptoPrices.push({
          symbol: crypto.ticker,
          name: crypto.name,
          price: parseFloat(ticker.lastPrice),
          priceChange24h: parseFloat(ticker.priceChange),
          priceChangePercent24h: parseFloat(ticker.priceChangePercent),
          volume24h: parseFloat(ticker.volume),
          iconUrl: `https://cryptoicons.org/api/icon/${crypto.ticker.toLowerCase()}/200`
        });
      }
    }

    return cryptoPrices;
  } catch (error) {
    console.error('❌ Binance API error:', error);
    throw new Error('Failed to fetch crypto prices');
  }
}

/**
 * Dobavi cenu za jednu kriptovalutu
 */
export async function getCryptoPrice(symbol: string): Promise<CryptoPrice | null> {
  try {
    const binanceSymbol = `${symbol.toUpperCase()}USDT`;
    const response = await axios.get(`${BINANCE_API_URL}/ticker/24hr`, {
      params: { symbol: binanceSymbol }
    });

    const ticker = response.data;
    const cryptoInfo = CRYPTO_SYMBOLS.find(c => c.symbol === binanceSymbol);

    if (!cryptoInfo) return null;

    return {
      symbol: cryptoInfo.ticker,
      name: cryptoInfo.name,
      price: parseFloat(ticker.lastPrice),
      priceChange24h: parseFloat(ticker.priceChange),
      priceChangePercent24h: parseFloat(ticker.priceChangePercent),
      volume24h: parseFloat(ticker.volume),
      iconUrl: `https://cryptoicons.org/api/icon/${cryptoInfo.ticker.toLowerCase()}/200`
    };
  } catch (error) {
    console.error(`❌ Error fetching price for ${symbol}:`, error);
    return null;
  }
}

/**
 * Dobavi istorijske podatke za grafikon (candlestick)
 */
export async function getCryptoChartData(
  symbol: string,
  interval: string = '1h',
  limit: number = 24
): Promise<any[]> {
  try {
    const binanceSymbol = `${symbol.toUpperCase()}USDT`;
    const response = await axios.get(`${BINANCE_API_URL}/klines`, {
      params: {
        symbol: binanceSymbol,
        interval: interval,
        limit: limit
      }
    });

    return response.data.map((candle: any) => ({
      time: candle[0],
      open: parseFloat(candle[1]),
      high: parseFloat(candle[2]),
      low: parseFloat(candle[3]),
      close: parseFloat(candle[4]),
      volume: parseFloat(candle[5])
    }));
  } catch (error) {
    console.error(`❌ Error fetching chart data for ${symbol}:`, error);
    return [];
  }
}

/**
 * Proveri da li je Binance API dostupan
 */
export async function checkBinanceHealth(): Promise<boolean> {
  try {
    const response = await axios.get(`${BINANCE_API_URL}/ping`);
    return response.status === 200;
  } catch (error) {
    return false;
  }
}