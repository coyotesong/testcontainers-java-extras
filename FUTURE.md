# Future Work

Possible future work.

## Email

[docker-mailserver/docker-mailserver](https://github.com/docker-mailserver/docker-mailserver) is a very robust self-contained
mail server that allows you to test both sending and receiving email. It also supports anti-spam and anti-virus software.

This is a natural choice since it eliminates the need to manage separate servers to send and receive email.

The exposed services would be:

- SMTP/S
- IMAP/S
- POP3/S

and possibly more.

## IdP

[freeipa/freeipa-server](https://hub.docker.com/r/freeipa/freeipa-server) is the most obvious choice but I know that they
have reported issues running inside of docker containers in the past. I don't know if that's still true - or if so if it
will affect a system only used for testing.

(I definitely know that we had problems with an earlier version of FreeIPA that we had to run inside of a docker container
due to restrictions by the operations team.)

The exposed services would be:

- LDAP
- Kerberos
- DNS

## OpenSSL + MIT KDC + Database?

Some organizations only require LDAP or a KDC, e.g., Hadoop clusters, and FreeIPA would be overkill. This would
be a standard OpenSSL (or 389?) container that:

- has the MIT KDC installed
- has loaded the Kerberos extensions to the LDAP server
- (maybe) has configured the LDAP server to use a relational database as the backend.

The relational database may seem like overkill but it's a clean way to test LDAP without relying on LDAP.
E.g., you can verify an LDAP ADD call by performing a database query, or add a database record and verify
you can read it with an LDAP query.

This combination isn't uncommon in the field, although in this case all maintenance is done via the
database server and the LDAP server is only available for queries.
