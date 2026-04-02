import mongoose, { Document, Schema } from 'mongoose';
import bcrypt from 'bcrypt';

export interface IUser extends Document {
  email: string;
  password: string;
  name: string;
  walletAddress: string;
  encryptedSeedPhrase: string;
  createdAt: Date;
  comparePassword(candidatePassword: string): Promise<boolean>;
}

const UserSchema = new Schema<IUser>({
  email: {
    type: String,
    required: [true, 'Email je obavezan'],
    unique: true,
    lowercase: true,
    trim: true,
    match: [/^\S+@\S+\.\S+$/, 'Unesite validan email']
  },
  password: {
    type: String,
    required: [true, 'Password je obavezan'],
    minlength: [6, 'Password mora imati minimum 6 karaktera']
  },
  name: {
    type: String,
    required: [true, 'Ime je obavezno'],
    trim: true
  },
  walletAddress: {
    type: String,
    required: true,
    unique: true
  },
  encryptedSeedPhrase: {
    type: String,
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Hash password pre čuvanja
UserSchema.pre('save', async function () {
  if (!this.isModified('password')) return;

  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
});

// Metoda za poređenje passworda
UserSchema.methods.comparePassword = async function(candidatePassword: string): Promise<boolean> {
  return bcrypt.compare(candidatePassword, this.password);
};

export default mongoose.model<IUser>('User', UserSchema);