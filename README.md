# CMU Architecture: Call-return and Tired Architectures
This is the A2 project for the "Architecture for Software System" couse at Carnegie Mellon University.
The highlight of this code is the implementation of a middle layer, or the middle server.
The middle layer takes in all user requests and redirects them to legacy systems which cannot be changed.
By doing so, we have these benefits:
- Legacy systems can be maintained to reduce cost
- Internal systems are hidden from outside for security
- New API can be exposed for new client side apps
- Zero down time guarenteed when updating, which is enabled by the  "API toggle" on the middle server
