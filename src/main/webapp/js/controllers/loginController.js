app.controller('loginController',function($scope){
  console.log("in loginController");

  var userName = "test123";
  var password = "test";
  var hashedPassword = cryptico.sha1(password);

  var RSASignKeys = cryptico.generateRSAKey("??"+password+"??"+userName+"??",512)

  var utcSeconds = 1234567890;
  var d = new Date(); // The 0 there is the key, which sets the date to the epoch
  var utc = d.toUTCString();
  var epoch = d.getTime();

  var signature = cryptico.RSASign(hashedPassword+":"+epoch,RSASignKeys);

  var accessToken = userName+":"+epoch+":"+signature;
  console.log(accessToken);

});
