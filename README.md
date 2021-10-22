# calculator
Demo project of a simple calculator that cals numerical results from strings.

The calculator is able to parse terms (strings) that follow certain conditions:
1. One operator per subterm. E.g. ((4+3)*(6-1))+4 works. 4+5-6 does not.
2. Numbers < 0 cannot be used in query.
3. Queries need to be encoded in base64. E.g. localhost/calculus?query=KCgxLjIrNSkgKyAoMSo1KSkgLTQ=
4. Calculator merely processes +,-,*,/ operations.
