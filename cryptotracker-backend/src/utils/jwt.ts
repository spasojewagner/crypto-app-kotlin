import jwt from 'jsonwebtoken';

// Required in production; a public default would let anyone sign tokens.
function jwtSecret(): string {
  const secret = process.env.JWT_SECRET;
  if (secret) return secret;
  if (process.env.NODE_ENV === 'production') throw new Error('JWT_SECRET must be set in production');
  return 'dev-only-jwt-secret';
}
const JWT_EXPIRES_IN = '7d';

export interface TokenPayload {
  userId: string;
  email: string;
}

export const generateToken = (payload: TokenPayload): string => {
  return jwt.sign(payload, jwtSecret(), { expiresIn: JWT_EXPIRES_IN });
};

export const verifyToken = (token: string): TokenPayload | null => {
  try {
    return jwt.verify(token, jwtSecret()) as TokenPayload;
  } catch (error) {
    return null;
  }
};