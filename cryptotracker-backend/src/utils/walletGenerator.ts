import { ethers } from 'ethers';
import crypto from 'crypto';

const ENCRYPTION_KEY = process.env.ENCRYPTION_KEY || 'your-32-char-secret-key-change-this-in-production!!';
const ALGORITHM = 'aes-256-cbc';

export interface WalletData {
  address: string;
  seedPhrase: string;
  encryptedSeedPhrase: string;
}

// Generiši novi wallet sa seed phrase
export function generateWallet(): WalletData {
  // Generiši random wallet
  const wallet = ethers.Wallet.createRandom();
  
  // Izvuci seed phrase (mnemonic)
  const seedPhrase = wallet.mnemonic?.phrase || '';
  
  // Enkriptuj seed phrase
  const encryptedSeedPhrase = encryptSeedPhrase(seedPhrase);
  
  return {
    address: wallet.address,
    seedPhrase: seedPhrase,
    encryptedSeedPhrase: encryptedSeedPhrase
  };
}

// Enkriptuj seed phrase
export function encryptSeedPhrase(seedPhrase: string): string {
  const iv = crypto.randomBytes(16);
  const key = crypto.scryptSync(ENCRYPTION_KEY, 'salt', 32);
  const cipher = crypto.createCipheriv(ALGORITHM, key, iv);
  
  let encrypted = cipher.update(seedPhrase, 'utf8', 'hex');
  encrypted += cipher.final('hex');
  
  return iv.toString('hex') + ':' + encrypted;
}

// Dekriptuj seed phrase (za kasnije - kada korisnik treba da vidi svoj seed)
export function decryptSeedPhrase(encryptedData: string): string {
  const parts = encryptedData.split(':');
  const iv = Buffer.from(parts[0], 'hex');
  const encryptedText = parts[1];
  
  const key = crypto.scryptSync(ENCRYPTION_KEY, 'salt', 32);
  const decipher = crypto.createDecipheriv(ALGORITHM, key, iv);
  
  let decrypted = decipher.update(encryptedText, 'hex', 'utf8');
  decrypted += decipher.final('utf8');
  
  return decrypted;
}

// Obnovi wallet iz seed phrase-a
export function restoreWalletFromSeed(seedPhrase: string): ethers.Wallet {
  return ethers.Wallet.fromPhrase(seedPhrase);
}