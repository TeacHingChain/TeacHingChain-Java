#!/usr/bin/env bash

echo "Generating private key"
openssl ecparam -genkey -name secp256k1 -rand /dev/urandom -out PRIVATE_KEY

# include ability to generate additional public keys?
#echo "Generating public key"
#openssl ec -in PRIVATE_KEY -pubout -out PUBLIC_KEY

echo "Generating TeacHingChain private key"
openssl ec -in PRIVATE_KEY -outform DER|tail -c +8|head -c 32|xxd -p -c 32 > THC_PRIVATE_KEY


