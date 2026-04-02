import express from 'express';
import { register, login, verifyTokenController } from '../controllers/authController';

const router = express.Router();

// POST /api/auth/register
router.post('/register', register);

// POST /api/auth/login
router.post('/login', login);

// POST /api/auth/verify
router.post('/verify', verifyTokenController);

export default router;