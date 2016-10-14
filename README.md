# CMU Architecture: Call-return and Tired Architectures
This is the A2 project for the "Architecture for Software System" course at Carnegie Mellon University.
The highlight of this code is the architectural design which introduces a middle layer, or the middle server.
The middle layer takes in all user requests and redirects them to legacy systems which cannot be changed.
By doing so, we have these benefits:
- Legacy systems can be maintained to reduce cost
- Internal systems are hidden from external access for security
- New API can be exposed for new client side apps
- Zero down time guarenteed when updating, which is enabled by the  "API toggle" on the middle server
- A web app can be easily implemented and hosted on the middle server
