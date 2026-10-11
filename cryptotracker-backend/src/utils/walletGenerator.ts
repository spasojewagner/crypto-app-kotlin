import { ethers } from 'ethers';
import crypto from 'crypto';

const DEV_ENCRYPTION_KEY = 'dev-only-encryption-key-change-me';

// Seed phrases are encrypted with this key. A missing key in production would mean every
// installation uses the same public default, so the server refuses to run without one.
function encryptionKey(): string {
  const key = process.env.ENCRYPTION_KEY;
  if (key) return key;
  if (process.env.NODE_ENV === 'production') throw new Error('ENCRYPTION_KEY must be set in production');
  return DEV_ENCRYPTION_KEY;
}
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
  const key = crypto.scryptSync(encryptionKey(), 'salt', 32);
  const cipher = crypto.createCipheriv(ALGORITHM, key, iv);
  
  let encrypted = cipher.update(seedPhrase, 'utf8', 'hex');
  encrypted += cipher.final('hex');
  
  return iv.toString('hex') + ':' + encrypted;
}

// Dekriptuj seed phrase (za kasnije - kada korisnik treba da vidi svoj seed)
export function decryptSeedPhrase(encryptedData: string): string {
  const [ivHex, encryptedText] = encryptedData.split(':');
  if (!ivHex || !encryptedText) throw new Error('Invalid encrypted seed phrase');
  const iv = Buffer.from(ivHex, 'hex');
  
  const key = crypto.scryptSync(encryptionKey(), 'salt', 32);
  const decipher = crypto.createDecipheriv(ALGORITHM, key, iv);
  
  let decrypted = decipher.update(encryptedText, 'hex', 'utf8');
  decrypted += decipher.final('utf8');
  
  return decrypted;
}

// Obnovi wallet iz seed phrase-a
export function restoreWalletFromSeed(seedPhrase: string): ethers.HDNodeWallet {
  return ethers.Wallet.fromPhrase(seedPhrase);
}