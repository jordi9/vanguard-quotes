package vanguard.quotes

import groovy.json.JsonSlurper
import ratpack.handling.RequestLogger
import ratpack.jackson.Jackson
import ratpack.server.RatpackServer

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.LocalDate

class App {

  static void main(String[] args) {
    RatpackServer.start(server -> server
      .handlers(chain -> chain
        .all(RequestLogger.ncsa())
        .get { ctx ->
          def params = new Params(
            startDate: ctx.request.queryParams['startDate'] ?: LocalDate.now().minusMonths(1).toString(),
            endDate: ctx.request.queryParams['endDate'] ?: LocalDate.now().toString()
          )
          def quotes = quotes(params)
          ctx.render(Jackson.json(new Response(data: quotes)))
        }
      )
    )
  }

  @SuppressWarnings("GrUnresolvedAccess")
  static List<Quote> quotes(Params params) {
    def client = HttpClient.newHttpClient()
    def request = HttpRequest.newBuilder()
      .uri(URI.create("https://api.vanguard.com/rs/gre/gra/1.7.0/datasets/gas-nl-inst-price-history-etf.json?" +
        "vars=portId:9503,issueType:F,sDate:${params.startDate},eDate:${params.endDate},as_of:DAILY"))
      .timeout(Duration.ofMinutes(1))
      .header("Content-Type", "application/json")
      .GET()
      .build()

    def response = client.send(request, HttpResponse.BodyHandlers.ofString())

    def json = new JsonSlurper().parseText(response.body())
    return json.fundPricesMktp[2].fundPrices.collect { new Quote(date: it.asOfDate, price: it.price) }
  }
}


class Params {
  String startDate
  String endDate
}

class Response {
  List<Quote> data
}

class Quote {
  String date
  BigDecimal price

  @SuppressWarnings("unused")
  String getDate() {
    return date[0..9]
  }

}
