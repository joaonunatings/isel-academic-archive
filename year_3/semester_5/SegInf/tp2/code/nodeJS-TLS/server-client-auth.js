var fs = require('fs'); 
var https = require('https'); 
var options = {
  key: fs.readFileSync('../../resources/secure-server/pem/secure-server_pfx.pem'),
  cert: fs.readFileSync('../../resources/secure-server/pem/secure-server.pem'),
  ca: [fs.readFileSync('../../resources/certs-pfx/cert-int/pem/CA1-int.pem'), fs.readFileSync('../../resources/certs-pfx/trust-anchors/pem/CA1.pem')],
  requestCert: true, // If true the server will request a certificate from clients that connect and attempt to verify that certificate
  rejectUnauthorized: true // If not false the server will reject any connection which is not authorized with the list of supplied CAs. This option only has an effect if requestCert is true.
}; 

var server = https.createServer(options, function (req, res) {
  console.log(new Date()+' '+
      req.socket.remoteAddress+' '+
      req.socket.getPeerCertificate().subject.CN+' '+
      req.method+' '+req.url);
  res.writeHead(200);
  res.end("Secure Hello World with node.js\n");
}).listen(4433);

server.on('uncaughtException', function (e) {
  // Handle your error here
  console.log(e);
});

console.log('Listening @4433');
