import mongoose, { Document, Schema } from 'mongoose';

export interface IPortfolioItem {
  symbol: string;
  name: string;
  amount: number;
  averageBuyPrice: number;
  isFavorite: boolean;
}

export interface IPortfolio extends Document {
  userId: mongoose.Types.ObjectId;
  items: IPortfolioItem[];
  totalBalanceUSD: number;
  lastUpdated: Date;
}

const PortfolioItemSchema = new Schema<IPortfolioItem>({
  symbol: {
    type: String,
    required: true
  },
  name: {
    type: String,
    required: true
  },
  amount: {
    type: Number,
    required: true,
    default: 0
  },
  averageBuyPrice: {
    type: Number,
    required: true,
    default: 0
  },
  isFavorite: {
    type: Boolean,
    default: false
  }
});

const PortfolioSchema = new Schema<IPortfolio>({
  userId: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    unique: true
  },
  items: [PortfolioItemSchema],
  totalBalanceUSD: {
    type: Number,
    default: 0
  },
  lastUpdated: {
    type: Date,
    default: Date.now
  }
});

export default mongoose.model<IPortfolio>('Portfolio', PortfolioSchema);