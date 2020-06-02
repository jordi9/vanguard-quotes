# vanguard-quotes

Small server to get [historical quotes](https://www.vanguard.nl/portal/instl/nl/en/product.html#/fundDetail/etf/portId=9503/assetCode=EQUITY/?prices) from Vanguard APIs and feed Portfolio Performance with them.

Run:

```
ge build installDist
build/install/vanguard-quotes/bin/vanguard-quotes
```

In Portfolio Performance:

* Historical Quotes > JSON
* Feed URL: `http://localhost:5050/?startDate=2020-04-03`
* Path to date: `$.data[*].date`
* Path to close: `$.data[*].price`
