# SecureWebChatApp
Secure Web Chat App that provide users with end to end encryption

# Security Features:
SecureWebChatApp use AES for symmetric encryption, RSA for Asymmetric encryption and signatures and SHA-1 for hashing
 
## 1-Registration
For registration a User has to provide a unique userName, password and generate two RSA key pairs one for Asymmetric encryption and one for signatures. UserName, password and others are used as salts to generate those keys in order to be able to generate the same key pairs at any time from any browser. The keys are changed once the password and/or userName is changed.
The new registered user will send userName, hashed password and two public keys. The request sent to server is:

         {userName}-Enc(serverEncPublicKey,{hashedPassword})-{encryptionPublicKey}-{signaturePublicKey}-Signature
    User------------------------------------------------------------------------------------------------------->Server
    
    Where Signature = Sign(clientSignPrivateKey,{userName}-Enc(serverEncPublicKey,{hashedPassword})-{encryptionPublicKey}-{signaturePublicKey})
   
Hashed password is encrypted using server's public key to ensure **confidentiality of the password**. Signature is used to ensure **integrity of the data** send to the server can verify if the request body was forged.

## 2-Authentication(login)
Every request between the user and server must contain header with name **"x-access-token"** which is a token used to **authenticate** user and **prevent reply attacks**. The token send in any request header is:

     x-access-token = {userName}:{date}:Signature
     Signature = Sign(clientSignPrivateKey,{HashedPassword}:{date})
 
  - The server will verify that there exist a user with the passed userName. <br />
  - The server will also check that the date is a valid date by checking that the date sent was from at most 3 minutes and       this delay time can be changed from configs.properties resource file. By checking that the date is valid we are able         to mitigate reply attacks. <br />
  - The server will load HashedPassword stored in the database to verify signature.<br />
  - If all the previous steps are valid then the user is authenticated to the system.

When the user use the login page to login to the system. User has to create **"x-access-token"** and add it to the header. If the user has been authenticated by the server then he/she will be redirected to home page. 

We signature because we only want to maintain **data integrity**. There are **no confidential data** that is sent. Hashed password was not sent it is part of signature and the server will load it from its database to verify signature.


## 3-Contacts
For a user to begin a chat, he/she must choose a receiver. So user must request all contacts(users) in the system to choose who he/she wants to chat with. The server will reply with the userNames of the contacts(users). There is an options to add offset and limit to fetch part of the available contacts(users) in the server.

Server will send all contacts as an array of userNames and also send signature. the response of the server is:
       
           {"contacts":UserNames[] , "signature":Signature} 
    Server----------------------------------------------->User
    
    Signature = Sign(ServerSignPrivateKey,UserNames[])

The server does not need to do any encryptions because the userNames sent are public to any one. The server generate signature of the sent data to maintain **data integrity** of the data and allow the client to check the data was not forged in the middle.

User has to create **"x-access-token"** and add it to the header. If the user has been authenticated by the server the server will proceed with creating and sending response.

## 4-Chat
The user has to choose a user to chat with. If it is the first time for them to chat with each, they will have to intiate connection between them by generating a chat AES symmetric key to encrypt all their messages.

User has to create **"x-access-token"** and add it to the header of each of the following requests. If the user has been authenticated by the server then the server will proceed with execution, constructing response and send it.

- For the first time the sender has to generate an AES key encrypt it with its public key and encrypt it again with receiver public key and then send it to the server. The request sent to the server is:
      
    ChatKey = GenerateAESKey()
    keyEncBySender = Enc(SenderEncPublickey , ChatKey)
    keyEncByReceiver = Enc(ReceiverEncPublicKey , ChatKey)
    Signature = Sign(SenderSignPrivateKey, keyEncBySender:keyEncByReceiver)
   
         {"keyEncBySender":keyEncBySender, "keyEncByReceiver":keyEncByReceiver, "signature":Signature}
      User------------------------------------------------------------------------------------------------>Server
 
  The server will store keyEncBySender to be used by user1 for messages encryption and decryption and keyEncByReceiver to be used by user2 for messages encryption and decryption. So no one except the two users know the chat key.

- For the next times where the any user of this two users want to send or show old messages of other user will first request chat key from server which will be:
       
    encryptedChatKey = chat key between requesting user and the other user which was encrypted with requesting user public Key   
    signature = Sign(ServerSignPrivateKey , encryptedChatKey)
    
           encryptedChatKey:signature
      Server------------------------------>User

  We use signature to make maintain data integrity by allowing the user to check whether the encryptedChatKey was forged in the pass between server and the user.

- For the user to send a new message after receiving encrypted chat key with his/her public key. the user will decrypt encryptedChatKey using his/her private key for encryption after verifing the signature.

      ChatKey = Dec(UserEncPrivateKey , encryptedChatKey)
      
  Then the user will encrypt message using AES symmetric key ChatKey and send it to server.
 
      encryptedMessage = AESEnc(ChatKey, message)
      signature = Sign(UserSignPrivateKey , encryptedMessage)
          {"encMessage":encryptedMessage,"signature":signature}
      User-------------------------------------------------->Sever 
 
   The server will store encryptedMessage, its timestamp (time of receiving), sender and receiver, after verifying signature to make sure that the encryptedMessage was not forged.
   
- For user to get old messages after choosing the other user. He/She will request old messages from server after requesting encrypted chat key and decrypt it using his/her RSA encryption private key to get AES chat key for the messaging connection between the two user. The server old message response will be:

      messages[] = an array of encrypted messages and its time stamps
      signature = stringFy(inbox[])- -Sign(ServerSignPrivateKey, Stringfy(messages[])). 
      where Stringfy() is used to convert an array to String
            {"messages":messages[], "signature":signature}
      Server----------------------------------------------->User
     
## 5-Inbox
The user is able to request from server his/her inbox. The server will response with the last message sent from any user and its timestamp. Ex: 
       
    senderUserName = userName of the sender who send a message to the requesting user when he/she was offline

    message = is the last message sent from sender to only show the requesting user the last message. It                will enhance user experience.

    timestamp = time of this last message sent.
    
    encryptedChatKey = Chat key encrypted by requesting user public key (it was stored in the database at the chat init  procedure).

    inbox[] = array of objects where object = {"sender":senderUserName, "message":message, "timeStamp":timeStamp, "encryptedChatKey":encryptedChatKey}

    signature = stringFy(inbox[])-Sign(ServerSingPrivateKey, stringFy(inbox[]))
    
           {"inbox":inbox[],"signature":signature}          
    Server----------------------------------------->User

The message is encrypted with chat key between sender and the requesting user. The encryptedChatKey is the chat key between two users and it is encrypted by requesting user public key. We use signature here to maintain **data integrity** and allow client to verify if the data was forged in the middle or not.

User has to create **"x-access-token"** and add it to the header the above request. If the user has been authenticated by the server then the server will proceed with execution, constructing response and send it.











  
