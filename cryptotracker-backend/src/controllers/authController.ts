import { Request, Response } from 'express';
import User from '../models/User';
import { generateToken, verifyToken } from '../utils/jwt';
import { generateWallet } from '../utils/walletGenerator';

// =======================
// REGISTER
// =======================
export const register = async (req: Request, res: Response): Promise<void> => {
  try {
    const { email, password, name } = req.body;

    if (!email || !password || !name) {
      res.status(400).json({
        success: false,
        message: 'Sva polja su obavezna'
      });
      return;
    }

    if (password.length < 6) {
      res.status(400).json({
        success: false,
        message: 'Password mora imati minimum 6 karaktera'
      });
      return;
    }

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      res.status(400).json({
        success: false,
        message: 'Korisnik već postoji'
      });
      return;
    }

    // 🔥 GENERATE WALLET
    const walletData = generateWallet();

    const user = new User({
      email,
      password,
      name,
      walletAddress: walletData.address,
      encryptedSeedPhrase: walletData.encryptedSeedPhrase
    });

    await user.save();

    const token = generateToken({
      userId: user._id.toString(),
      email: user.email
    });

    res.status(201).json({
      success: true,
      message: 'Registracija uspešna! SAČUVAJ SEED PHRASE!',
      data: {
        token,
        user: {
          id: user._id,
          email: user.email,
          name: user.name,
          walletAddress: user.walletAddress
        },
        wallet: {
          address: walletData.address,
          seedPhrase: walletData.seedPhrase // ⚠️ SAMO OVDE
        }
      }
    });
  } catch (error) {
    console.error('❌ Register error:', error);
    res.status(500).json({
      success: false,
      message: 'Greška na serveru'
    });
  }
};

// =======================
// LOGIN
// =======================
export const login = async (req: Request, res: Response): Promise<void> => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      res.status(400).json({
        success: false,
        message: 'Email i password su obavezni'
      });
      return;
    }

    const user = await User.findOne({ email });
    if (!user || !(await user.comparePassword(password))) {
      res.status(401).json({
        success: false,
        message: 'Pogrešan email ili password'
      });
      return;
    }

    const token = generateToken({
      userId: user._id.toString(),
      email: user.email
    });

    res.status(200).json({
      success: true,
      message: 'Login uspešan',
      data: {
        token,
        user: {
          id: user._id,
          email: user.email,
          name: user.name,
          walletAddress: user.walletAddress
        }
      }
    });
  } catch (error) {
    console.error('❌ Login error:', error);
    res.status(500).json({
      success: false,
      message: 'Greška na serveru'
    });
  }
};

// =======================
// VERIFY TOKEN
// =======================
export const verifyTokenController = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { token } = req.body;

    if (!token) {
      res.status(400).json({
        success: false,
        message: 'Token nije prosleđen'
      });
      return;
    }

    const payload = verifyToken(token);
    if (!payload) {
      res.status(401).json({
        success: false,
        message: 'Nevažeći ili istekao token'
      });
      return;
    }

    const user = await User.findById(payload.userId).select(
      '-password -encryptedSeedPhrase'
    );

    if (!user) {
      res.status(404).json({
        success: false,
        message: 'Korisnik nije pronađen'
      });
      return;
    }

    res.status(200).json({
      success: true,
      data: {
        user: {
          id: user._id,
          email: user.email,
          name: user.name,
          walletAddress: user.walletAddress
        }
      }
    });
  } catch (error) {
    console.error('❌ Verify token error:', error);
    res.status(500).json({
      success: false,
      message: 'Greška na serveru'
    });
  }
};
