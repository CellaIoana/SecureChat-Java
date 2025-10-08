README.md (Markdown content)
# 🔐 SecureChat — Java (Sockets) with Diffie–Hellman + AES

[![Java CI with Maven](https://github.com/CellaIoana/SecureChat-Java/actions/workflows/maven.yml/badge.svg)](https://github.com/CellaIoana/SecureChat-Java/actions/workflows/maven.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java](https://img.shields.io/badge/Java-23-blue)

Java sockets chat using AES encryption and Diffie–Hellman key exchange.

Console chat over TCP sockets with a **secure channel**:
- **Diffie–Hellman** key exchange (per connection)
- **AES** message encryption (GCM-ready; current impl uses hashed shared secret)

## ✨ Features
- Server accepts multiple clients (thread per client)
- DH key exchange; shared secret derived & hashed for AES key bytes
- AES encrypt/decrypt for chat messages
- Simple CaesarCipher kept for comparison/learning

## ⚙️ Requirements
- JDK 17+ (tested with JDK 23)
- (Optional) `lib/` with extra JARs if your IDE setup needs them

## 🧭 How it works (crypto)
1. **Server** generates a DH key pair and sends **server public key** to client.
2. **Client** extracts DH params from server public key, **generates its key pair with the same params**, sends **client public key** back.
3. Both sides compute the **shared secret** via `KeyAgreement`.
4. Shared secret is hashed → 16 bytes used as AES key (for demo).
5. Messages are AES-encrypted before sending; decrypted on receive.

## 🚀 Run (from IDE)
1. Run `Server.java`  
2. Run `Client.java` (one or more times)  
Type messages in the client console; server broadcasts to all clients.

> If you see “Illegal base64 character”, it means a non-Base64 line reached the decrypt step.
> (We’ll harden the protocol soon: message framing + type tags).

## 🧱 Project Structure
```
src/
  module-info.java
  server_client_chat_app/
    Server.java
    Client.java
    ClientHandler.java
    AESUtil.java
    CaesarCipher.java
    DiffieHellmanUtil.java
lib/           # optional jars
docs/          # screenshots/diagrams (optional)
```

## 🗺️ Roadmap
- [ ] Switch to AES-GCM with IV + auth tag
- [ ] Message framing (length + type)
- [ ] Basic UI (JavaFX) and user names
- [ ] Unit tests for DH/AES utilities
- [ ] CI build (once we add Maven/Gradle)

## 🏷️ Topics
`java` `sockets` `diffie-hellman` `aes` `cryptography` `client-server`

## 📝 License
MIT — see LICENSE.



