import dotenv from 'dotenv';
import express, { Application, Request, Response } from 'express';
import cors from 'cors';
import mongoose from 'mongoose';
import authRoutes from './routes/authRoutes';
import cryptoRoutes from './routes/cryptoRoutes';

// Učitaj env varijable
dotenv.config();

const app: Application = express();
const PORT = process.env.PORT || 5000;

// ========== MIDDLEWARE ==========
app.use(cors({
  origin: '*',
  credentials: true
}));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// ========== ROUTES ==========
app.use('/api/auth', authRoutes);
app.use('/api/crypto', cryptoRoutes);

// Health check
app.get('/health', (req: Request, res: Response) => {
  res.status(200).json({ 
    status: 'OK', 
    message: 'CryptoTracker API is running',
    timestamp: new Date().toISOString()
  });
});

// 404 handler
app.use((req: Request, res: Response) => {
  res.status(404).json({ 
    success: false, 
    message: 'Route not found' 
  });
});

// ========== MONGODB CONNECTION ==========
const connectDB = async (): Promise<void> => {
  try {
    const mongoURI = process.env.MONGODB_URI || 'mongodb://localhost:27017/cryptotracker';
    
    await mongoose.connect(mongoURI);
    
    console.log('✅ MongoDB connected successfully');
    console.log(`📦 Database: ${mongoose.connection.name}`);
    
  } catch (error) {
    console.error('❌ MongoDB connection error:', error);
    process.exit(1);
  }
};

// MongoDB event listeners
mongoose.connection.on('disconnected', () => {
  console.log('⚠️  MongoDB disconnected');
});

mongoose.connection.on('error', (err) => {
  console.error('❌ MongoDB error:', err);
});

// ========== START SERVER ==========
const startServer = async () => {
  try {
    await connectDB();

    // BITNO: Mora biti '0.0.0.0' da bi telefon mogao da pristupi!
    app.listen(PORT, '0.0.0.0', () => {
      console.log('╔════════════════════════════════════════╗');
      console.log('║  🚀 CryptoTracker Backend Server      ║');
      console.log('╠════════════════════════════════════════╣');
      console.log(`║  📡 Port: ${PORT}                       ║`);
      console.log(`║  🌍 Listening on: 0.0.0.0              ║`);
      console.log(`║  🔗 URL: http://192.168.0.100:5000:${PORT}    ║`);
      console.log('╚════════════════════════════════════════╝');
    });

  } catch (error) {
    console.error('❌ Server startup failed:', error);
    process.exit(1);
  }
};

// Pokreni server
startServer();

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('👋 SIGTERM received, shutting down gracefully');
  mongoose.connection.close();
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('👋 SIGINT received, shutting down gracefully');
  mongoose.connection.close();
  process.exit(0);
});
