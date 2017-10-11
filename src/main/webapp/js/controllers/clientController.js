angular.module('mainApp').controller('ClientController',function($scope){
// console.log("clientController");
var userName1 = "RaniaWael";
var password1= "1234";
var RSAKeys1 = cryptico.generateRSAKey(userName1+"?"+password1, 512);
var publicKeyStr1 = cryptico.publicKeyString(RSAKeys1);

$scope.client1 = {
  userName: userName1,
  password: password1,
  RSAKeys:RSAKeys1,
  publicKeyStr: publicKeyStr1
};

var userName2 = "HatemMorgan";
var password2= "1995";
var RSAKeys2 = cryptico.generateRSAKey(userName2+"?"+password2, 512);
var publicKeyStr2 = cryptico.publicKeyString(RSAKeys2);

$scope.client2 = {
  userName: userName2,
  password: password2,
  RSAKeys: RSAKeys2,
  publicKeyStr:publicKeyStr2
};


console.log("In Client controller");

console.log($scope.messageModel);

$scope.printMessage = function(){
  console.log("Plain Message: "+$scope.messageModel);
  console.log("Client1 Public Key"+ $scope.client1.publicKeyStr);
  console.log("Client2 Public Key"+ $scope.client2.publicKeyStr)
}

$scope.encryptMessage = function(){
  $scope.printMessage();
  var encryptedMessage = cryptico.encrypt($scope.messageModel,$scope.client2.publicKeyStr);
  console.log("EncryptedMessage by Client1: "+ encryptedMessage.cipher);

  var decryptedMessage = cryptico.decrypt(encryptedMessage.cipher,$scope.client2.RSAKeys);
  console.log("DecryptedMessage by Client 2: " + decryptedMessage.plaintext);
}
console.log("---->"+cryptico.getPubKey($scope.client2.RSAKeys));
 cryptico.RSADecrypt("fiatfxhna1kU9vA8m6REBShuHGFIPniC0sKxgdF0h7URULZaOp1i7RgTDrLCK+JdfAYQFUavOO+PUS14l83mzA==",$scope.client2.RSAKeys);
// cryptico.RSAEncrypt("Hatem",$scope.client2.publicKeyStr);
var n = "ac6e04127fb95c18435df0fcbcbee86d5a57656b57e516bc12b9abbd80451f1b548501278db91d588c2eac517fe07e972a8ca39b0cbb4d09af202a9fc0bfd44f";
var e = "0000000000000000000000000000000000010001";

var RSAKeys3 = cryptico.generateRSAKey("xx", 512);
cryptico.setPublicKey(RSAKeys3,n,e);

cryptico.RSAEncrypt("Hatem",RSAKeys3);

// signature test
var RSASignKey = cryptico.generateRSAKey(userName2+"!signature!"+password2, 512);
cryptico.RSASign("Hatem1995",RSASignKey);

var n2 = "878e9b2b686fe5efdf1074484cce3abf6bbf4d501ac0b1492b9ed94bdc32f69524e031bd37d7aa130f8192b26e7149cb8aad4b7e343c914dfbf06df696c96e67";
var e2= "0000000000000000000000000000000000010001";

var RSASignKeys = cryptico.generateRSAKey("xx", 512);
cryptico.setPublicKey(RSASignKeys,n2,e2);
var signature = "FV/m1nHtL6kC3qPBYqqGazLB/vAgJCuhyvXdenxf99ukxVp/aW7HgmTsRQG64VOjwHPF8IOLeDhNIb0sHfCf0A==";
cryptico.verify("Hatem",signature,RSASignKeys);

var utcSeconds = 1234567890;
var d = new Date(); // The 0 there is the key, which sets the date to the epoch
var utc = d.toUTCString();
var epoch = d.getTime();
console.log(epoch);

var hash = cryptico.sha1("1234");
console.log(hash.length);
console.log(hash);


var testRSAKey = cryptico.generateRSAKey("test"+"?"+"1234", 512);
console.log(testRSAKey.n+"");
console.log(cryptico.publicKeyString(testRSAKey));
var testPubKey = cryptico.getPubKey(testRSAKey).split(":");
console.log(testPubKey);
var testN = testPubKey[0];
var teste = testPubKey[1];
// console.log(teste);
// console.log(Number(teste).toString(16));
var testConstructedPubKey = cryptico.generateRSAKey("xx", 512);
cryptico.setPublicKey(testConstructedPubKey,testN,teste);
console.log(cryptico.publicKeyString(testConstructedPubKey));

var testCipher = "DeHDOfifP+y9RgcA4Loc2dm1mhoDrDTLKrykSbbidUvPkFrRN/J98CUrv9TJfZZlUVwpII1qQORuJy9Ah4pz2A==";
var testPlain = cryptico.RSADecrypt(testCipher,testRSAKey);
console.log(testPlain);
});
