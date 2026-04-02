import express from 'express';
import {
  getAllPrices,
  getSinglePrice,
  getChartData,
  getUserPortfolio,
  addToPortfolio,
  toggleFavorite,
  healthCheck
} from '../controllers/cryptoControllers';

const router = express.Router();

// Public routes (ne trebaju autentifikaciju)
router.get('/health', healthCheck);
router.get('/prices', getAllPrices);
router.get('/price/:symbol', getSinglePrice);
router.get('/chart/:symbol', getChartData);

// Portfolio routes (trebaju autentifikaciju - dodaj middleware kasnije)
router.get('/portfolio/:userId', getUserPortfolio);
router.post('/portfolio/add', addToPortfolio);
router.put('/portfolio/favorite', toggleFavorite);

export default router;