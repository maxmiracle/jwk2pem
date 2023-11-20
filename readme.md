# jwk2pem file converter for integration certBot and micronaut-acme

[micronaut-acme](https://micronaut-projects.github.io/micronaut-acme/latest/guide)
and 
[certBot](https://certbot.eff.org/) for LetsEncrypt

Problem: certbot generated file formats are different from micronaut-acme.

Java snippet can be used to convert certbot files to micronaut-acme format.

###### usage:

gradlew run --args="/pathToJwkAccountKey/private_key.json /pathToOutAccountKey/out.pem /pathToDomainPrivKey/privkey.pem /domainKeyOutPath/domainout.pem"