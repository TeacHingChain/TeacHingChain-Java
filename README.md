

TeacHingChain-Java - A very simple implementation of a rudimentary blockchain for illustrative/teaching purposes in Java

Current build: unstable 

The network side of the build is not yet complete, we will release an official, detailed changelog shortly after each commit.

INSTRUCTIONS FOR BUILDING/RUNNING PROJECT

    Use the genesis builder in the GenesisBuilder-Java repo to generate the genesis block and take note of the values.. OR you can use the one provided

    If you're building your own genesis block, navigate to the GenesisBlock.java class in com.thc.blockchain.wallet

    Replace the appropriate values (algo (only sha256 works right now), timestamp, pszTimestamp, genesis hash, target)

    On first run, the blockchain will be initialized and all of the appropriate keys will be generated and the program will exit

    Rerun the program and you will be greeted with the command prompt

NOTES

    We have added support for the maven dependencies plugin, so the classpath is now in the build directory

    This project is in it's infancy, there are many things that are being added/changed/worked on.. This is never meant to be production quality software, it's meant to be very simple and for illustrative purposes..

    We are amateur coders, there will be mistakes and better ways to code some of the elements of this project. Feel free to give us your thoughts on any of the code or any part of the project!

IN THE WORKS

    Adding web socket client and server end-points for block/tx transport across nodes using server push

    Began building GUI, only send tx and mining functionality, not yet separate from CLI (they still run concurrently)

    Adding support for SHA512 and scrypt algorithms

    Rewriting the target adjustment (which is very granular and not at all reminiscent of an actual target adjustment algorithm)

    Updating the miner class to allow for the discovery of tx's in the tx-pool, as well as the ability to throw away stale work

    Adding support for SHA512 and scrypt algorithms for the GenesisBuilder (in the GenesisBuilder-Java repo)

    Adding the ability to run the wallet as the GUI or as a daemon with a CLI

    Writing shell scripts for easy building/running (since the project will need to run as both a deployed war AND a jar when the network side is working)

    And more!

JOIN THE COMMUNITY, CONTRIBUTE!

If you are interested in contributing, email us @ TeacHingChain0420@gmail.com

We are open to suggestions, comments and contributions so please feel free to contact us!

More detailed technical specifications on the project are coming soon!
