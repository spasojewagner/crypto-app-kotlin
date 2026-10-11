# CryptoTracker

[![CI](https://github.com/spasojewagner/crypto-app-kotlin/actions/workflows/ci.yml/badge.svg)](https://github.com/spasojewagner/crypto-app-kotlin/actions/workflows/ci.yml)

An Android app for following crypto prices, with its own Node.js API. Every account gets an Ethereum wallet address when it is created.

**Stack:** Kotlin, Android SDK (minSdk 24), Retrofit, OkHttp, Coroutines, Glide, Material Components · Node.js, Express 5, TypeScript, MongoDB with Mongoose, ethers.js, Binance API

## Features

- **Sign-up and sign-in** with a JWT kept in SharedPreferences.
- **Wallet on sign-up:** the API creates an Ethereum wallet with ethers.js. The 12-word seed phrase is shown once, in a dialog asking the user to write it down. The server keeps it only encrypted (AES-256-CBC).
- **Live prices** for 35 coins from Binance (price, 24 h change, volume), refreshed every 30 seconds, with candlestick data for charts.
- **Portfolio:** holdings with average buy price, current value and profit or loss, plus favourites.
- **In-app test screen** that runs checks on validation, models, token storage and the adapter. JVM unit tests live in `app/src/test`.

## Running it

### API

You need Node.js 20+ and MongoDB.

```bash
cd cryptotracker-backend
cp .env.example .env
npm install
npm run dev          # listens on 0.0.0.0:5000 so a phone on the same Wi-Fi can reach it
```

### Android app

1. Open `cryptotracker-frontend` in Android Studio.
2. In `app/src/main/java/com/example/cryptotracker/api/RetrofitClient.kt`, set `BASE_URL` to your computer's address on the local network, for example `http://192.168.0.100:5000/api/`. On the emulator, use `http://10.0.2.2:5000/api/`.
3. Run the app. Unit tests: `./gradlew testDebugUnitTest`.

## API

| Route | |
|---|---|
| `POST /api/auth/register` | creates the account and wallet; returns the token, user and seed phrase (once) |
| `POST /api/auth/login`, `POST /api/auth/verify` | sign-in, token check |
| `GET /api/crypto/prices`, `/api/crypto/price/:symbol` | prices from Binance |
| `GET /api/crypto/chart/:symbol?interval=1h&limit=24` | candlesticks |
| `GET /api/crypto/portfolio/:userId`, `POST /api/crypto/portfolio/add`, `PUT /api/crypto/portfolio/favorite` | portfolio |
| `GET /health` | liveness |

## Limitations

- The portfolio routes take the user id from the request instead of the JWT, so they aren't protected yet. The next step is an auth middleware on the API and an OkHttp interceptor in the app that sends the token.
- The API address is set in code (`RetrofitClient.kt`), not in a build setting.

GitHub Actions type-checks and builds the API on every push.
