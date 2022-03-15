package com.xassure.framework.driver;

import com.google.inject.Inject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;
import io.restassured.RestAssured;

import java.io.File;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

public class ApiControlsLibrary implements RestControls {

    @Inject
    public ApiControlsLibrary() {

    }

    public static void main(String[] args) throws Exception {

        Response response = given()
                .when().get("https://reg-002.levi-site.Mocom/CA/en_CA/sitemap.xml").then().extract()
                .response();
        System.out.println(response.getBody().prettyPrint());
    }

    private static void productQueryExample() throws Exception {
        ApiControlsLibrary apiControlsLibrary = new com.xassure.framework.driver.ApiControlsLibrary();
        String query = "{\"query\":\"query product($code: String!) {\\n        product(code: $code) {\\n            altText\\n            averageOverallRatings\\n            baseProduct\\n            channels\\n            classifications {\\n                code\\n                features {\\n                    code\\n                    comparable\\n                    description\\n                    featureUnit {\\n                        name\\n                        symbol\\n                        unitType\\n                    }\\n                    featureValues {\\n                        value\\n                    }\\n                    name\\n                    range\\n                    type\\n                }\\n                name\\n            }\\n            code\\n            colorGroup\\n            colorName\\n            comingSoon\\n            customizable\\n            customizationType\\n            description\\n            findInStoreEligible\\n            flxCustomization\\n            galleryImageList {\\n                galleryImage {\\n                    altText\\n                    format\\n                    galleryIndex\\n                    imageType\\n                    url\\n                }\\n                videos {\\n                    altText\\n                    format\\n                    galleryIndex\\n                    imageType\\n                    url\\n                }\\n            }\\n            hassleFreeEnabled\\n            lscoBreadcrumbs {\\n                categoryCode\\n                name\\n                url\\n            }\\n            \\n            itemType\\n            maxOrderQuantity\\n            merchantBadge\\n            minOrderQuantity\\n            multidimensional\\n            name\\n            noOfRatings\\n            potentialPromotions {\\n                code\\n                couldFireMessages\\n                couponCode\\n                description\\n                enabled\\n                endDate\\n                firedMessages\\n                priority\\n                productBanner {\\n                    altText\\n                    format\\n                    galleryIndex\\n                    imageType\\n                    url\\n                }\\n                promotionGroup\\n                promotionType\\n                restrictions {\\n                    description\\n                    restrictionType\\n                }\\n                startDate\\n                title\\n            }\\n            price {\\n                code\\n                currencyIso\\n                formattedValue\\n                hardPrice\\n                hardPriceFormattedValue\\n                maxQuantity\\n                minQuantity\\n                priceType\\n                regularPrice\\n                regularPriceFormattedValue\\n                softPrice\\n                softPriceFormattedValue\\n                value\\n            }\\n            productSchemaOrgMarkup {\\n                aggregateRating {\\n                    entry {\\n                        key\\n                        value\\n                    }\\n                }\\n                brand {\\n                    entry {\\n                        key\\n                        value\\n                    }\\n                }\\n                gtin12\\n                offers {\\n                    entry {\\n                        key\\n                        value\\n                    }\\n                }\\n            }\\n            promotionalBadge\\n            purchasable\\n            seoMetaData {\\n                canonicalUrl\\n                metaDescription\\n                metaH1\\n                metaTitle\\n                robots\\n            }\\n            sizeGuide\\n            soldIndividually\\n            soldOutForever\\n            summary\\n            url\\n            variantLength\\n            variantMatrix\\n            variantOptions {\\n                code\\n                colorName\\n                displaySizeDescription\\n                maxOrderQuantity\\n                minOrderQuantity\\n                priceData {\\n                    code\\n                    currencyIso\\n                    formattedValue\\n                    hardPrice\\n                    hardPriceFormattedValue\\n                    maxQuantity\\n                    minQuantity\\n                    priceType\\n                    regularPrice\\n                    regularPriceFormattedValue\\n                    softPrice\\n                    softPriceFormattedValue\\n                    value\\n                }\\n                stock {\\n                    stockLevel\\n                    stockLevelStatus\\n                }\\n                url\\n                variantOptionQualifiers {\\n                    image {\\n                        altText\\n                        format\\n                        galleryIndex\\n                        imageType\\n                        url\\n                    }\\n                    name\\n                    qualifier\\n                    value\\n                }\\n            }\\n            variantSize\\n            variantType\\n            variantWaist\\n            errors {\\n                component\\n                name\\n                time_thrown\\n                message\\n           }\\n    }\\n}\",\n" +
                "  \"variables\":{\"code\":\"%s\"}}";

        String productID = "178470003";
        query = String.format(query, productID);
        Map<String, String> headers = new java.util.HashMap<>();
        headers.put("country", "US");
        headers.put("locale", "en_US");
        headers.put("brand", "levi");
        Response response = given().headers(headers).body(query).contentType("application/json").expect()
                .statusCode(200).when().post("https://sprint-400.levi-site.com/nextgen-webhooks/").then().extract().response();
        DocumentContext documentContext = JsonPath.parse(response.getBody().print());
//        documentContext.read("$..variantOptions");
        documentContext.read("$..variantOptions[?(@.stock.stockLevelStatus=='outOfStock')]");
        int totalCount = response.path("data.search.pagination.totalResults");
        System.out.println(totalCount);

    }

    private static void searchQueryExample() throws Exception {
        ApiControlsLibrary apiControlsLibrary = new com.xassure.framework.driver.ApiControlsLibrary();
        String query = "{\"query\":\"query swatches($code: String!) {\\n        swatches(code: $code) {\\n            code\\n            selectedSwatch {\\n                active\\n                available\\n                code\\n                colorName\\n                imageUrl\\n                url\\n                variantsAvailability {\\n                    available\\n                    length\\n                    size\\n                    waist\\n                }\\n            }\\n            swatches {\\n                active\\n                available\\n                code\\n                colorName\\n                imageUrl\\n                url\\n                variantsAvailability {\\n                    available\\n                    length\\n                    size\\n                    waist\\n                }\\n            }\\n            errors {\\n                component\\n                name\\n                time_thrown\\n                message\\n            }\\n        }\\n    }\",\"variables\":{\"code\":\"193420018\"}}";
        query = "{\"query\":\"query search(\\n  $query: String!\\n  $currentPage: Int\\n  $pageSize: Int\\n  $sort: String\\n) {\\n  search(\\n    query: $query\\n    currentPage: $currentPage\\n    pageSize: $pageSize\\n    sort: $sort\\n  ) {\\n    type\\n    breadcrumbs {\\n      facetCode\\n      facetName\\n      facetValueCode\\n      facetValueName\\n      removeQuery {\\n        query {\\n          value\\n        }\\n        url\\n      }\\n      truncateQuery {\\n        query {\\n          value\\n        }\\n        url\\n      }\\n    }\\n    categoryCode\\n    categoryHierarchy {\\n      code\\n      count\\n      selected\\n      childSelected\\n      children\\n      depth\\n      leaf\\n      parentSelected\\n    }\\n    categoryName\\n    currentQuery {\\n      query {\\n        value\\n      }\\n      url\\n    }\\n    facets {\\n      category\\n      multiSelect\\n      name\\n      priority\\n      visible\\n      code\\n      topValues {\\n        count\\n        name\\n        selected\\n        query {\\n          query {\\n            value\\n          }\\n          url\\n        }\\n      }\\n      values {\\n        count\\n        name\\n        selected\\n        query {\\n          query {\\n            value\\n          }\\n          url\\n        }\\n      }\\n    }\\n    freeTextSearch\\n    keywordRedirectUrl\\n    lscoBreadcrumbs {\\n      name\\n      url\\n      linkClass\\n    }\\n    pagination {\\n      currentPage\\n      pageSize\\n      sort\\n      totalPages\\n      totalResults\\n    }\\n    products {\\n      code\\n      name\\n      url\\n      swatchUrl\\n      swatchAltText\\n      galleryList {\\n        galleryImage {\\n          altText\\n          format\\n          galleryIndex\\n          imageType\\n          url\\n        }\\n      }\\n      purchasable\\n      price {\\n        code\\n        currencyIso\\n        formattedValue\\n        hardPrice\\n        hardPriceFormattedValue\\n        regularPrice\\n        regularPriceFormattedValue\\n        softPrice\\n        softPriceFormattedValue\\n        value\\n      }\\n      baseProduct\\n      soldIndividually\\n      colorName\\n      colorGroup\\n      comingSoon\\n      averageOverallRatings\\n      noOfRatings\\n      soldOutForever\\n      sustainability\\n      findInStoreEligible\\n      customizable\\n      flxCustomization\\n      variantOptions {\\n        code\\n        comingSoon\\n        customizable\\n        findInStoreEligible\\n        flxCustomization\\n        swatchUrl\\n        swatchAltText\\n        galleryList {\\n          galleryImage {\\n            altText\\n            format\\n            galleryIndex\\n            imageType\\n            url\\n          }\\n        }\\n        merchantBadge\\n        promotionalBadge\\n        sustainability\\n        name\\n        priceData {\\n          hardPrice\\n          hardPriceFormattedValue\\n          regularPrice\\n          regularPriceFormattedValue\\n          softPrice\\n          softPriceFormattedValue\\n          value\\n          currencyIso\\n        }\\n        soldIndividually\\n        soldOutForever\\n        url\\n      }\\n      lscoBreadcrumbs {\\n        categoryCode\\n        name\\n        url\\n      }\\n      merchantBadge\\n      promotionalBadge\\n      errors {\\n        component\\n        name\\n        time_thrown\\n        message\\n      }\\n    }\\n    seoMetaData {\\n      canonicalUrl\\n      metaDescription\\n      metaH1\\n      metaTitle\\n    }\\n    sorts {\\n      code\\n      name\\n      selected\\n    }\\n    spellingSuggestion {\\n      query\\n      suggestion\\n    }\\n  }\\n}\",\"variables\":{\"currentPage\":0,\"pageSize\":30,\"query\":\"%s\",\"sort\":\"relevance\"}}";
        String search = "jean";
        query = String.format(query, search);
        Map<String, String> headers = new java.util.HashMap<>();
        headers.put("country", "US");
        headers.put("locale", "en_US");
        headers.put("brand", "levi");


//        ResponseBodyExtractionOptions responseBodyExtractionOptions = given().header("Accept", "application/json").body(query).contentType("application/json").expect()
//                .statusCode(200).when().post("https://sprint-400.levi-site.com/nextgen-webhooks/").then().extract().body();
        Response response = given().headers(headers).body(query).contentType("application/json").expect()
                .statusCode(200).when().post("https://sprint-400.levi-site.com/nextgen-webhooks/").then().extract().response();

        int totalCount = response.path("data.search.pagination.totalResults");
        System.out.println(totalCount);
        String filePath = "./src/test/resources/testData/SearchQuery.json";
        DocumentContext jsonPath = JsonPath.parse(new File(filePath));
        String searchString = jsonPath.read("$.variables.query");
        System.out.println(searchString);
        jsonPath.put("$.variables", "query", "jeans");
        query = jsonPath.jsonString();
        System.out.println("Updated Query: " + query);
        response = given().headers(headers).body(query).contentType("application/json").expect()
                .statusCode(200).when().post("https://sprint-400.levi-site.com/nextgen-webhooks/").then().extract().response();

        totalCount = response.path("data.search.pagination.totalResults");
        System.out.println(totalCount);
    }

    public Response getResponse(String uri) {

        Response response = given()
                .when().get(uri).then().extract()
                .response();

        System.out.println("URL " + uri + " Status Code: ---- " + response.statusCode());
        return response;
    }

    @Override
    public ResponseBodyExtractionOptions GetRequestWithOauth(String url) {
        ResponseBodyExtractionOptions response = null;
        try {
            long start = System.currentTimeMillis();
            response = given().header("Accept", "application/json").contentType(ContentType.JSON).expect()
                    .statusCode(200).when().get(url).then().extract().body();
            long end = System.currentTimeMillis();
            if (response != null) {
                Reporting.getLogger().logApiResponseTime(LogStatus.PASS, "Successful hit to Users API Endpoint : " + url, (end - start) / 1000 + " secs");

            } else {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to access Users API Endpoint : " + url);

            }
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseBodyExtractionOptions GetRequestWithGraphQL(String url, String query) {
        return given().header("Accept", "application/json").body(query).contentType("application/json").expect()
                .statusCode(200).when().post(url).then().extract().body();
    }

    @Override
    public Response GetRequestWithGraphQL(String url, String query, Map<String, ?> headers) {
        return given().headers(headers).body(query).contentType("application/json").expect()
                .when().post(url);
    }

    @Override
    public String makeGetRequestAndGetBody(String url) {
        System.out.println("URL is : " + url);
        return given().get(url).body().asString();
    }

    @Override
    public Response makeGetRequestAndGetResponse(String url) {
        return given().get(url);
    }

    @Override
    public int makeGetRequestAndGetStatusCode(String url) {
        return given().get(url).getStatusCode();
    }

    @Override
    public String makeGetRequestAndGetContentType(String url) {
        return given().get(url).getContentType();
    }

    @Override
    public String makeGetRequestAndGetHeaders(String url, String headerName) {
        return given().get(url).getHeader(headerName);
    }


    /**
     * Get Request for given :
     *
     * @param baseUri
     * @param endpoint
     * @param headers
     * @return
     */
    public io.restassured.response.Response getRequest(String baseUri,
                                                       String endpoint,
                                                       Map<String, ?> headers) {
        io.restassured.response.Response response = null;
        try {
            long start = System.currentTimeMillis();
            response = RestAssured.given()
                    .baseUri(baseUri)
                    .headers(headers)
                    .contentType(io.restassured.http.ContentType.JSON)
                    .expect()
                    .statusCode(200)
                    .when().get(endpoint);

            long end = System.currentTimeMillis();
            String url = baseUri + endpoint;
            Reporting.getLogger().log(LogStatus.INFO, "Value of response code for endpoint " + endpoint + " is " + response.statusCode());
            Reporting.getLogger().log(LogStatus.INFO, "Response is "
                    + "\"" + response.prettyPrint() + "\"");
            if (response != null) {
                Reporting.getLogger().logApiResponseTime(LogStatus.PASS, "Successful hit to Users API Endpoint : " + url, (end - start) / 1000 + " secs");

            } else {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to access Users API Endpoint : " + url);

            }

            return response;

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while getting response from url : " +
                    "" + baseUri + endpoint);

            return null;
        }
    }

    /**
     * Get Request for given uri with path parameters :
     *
     * @param baseUri
     * @param endpoint
     * @param params
     * @param headers
     * @return
     */
    public io.restassured.response.Response getRequest(String baseUri,
                                                       String endpoint,
                                                       Map<String, ?> headers,
                                                       Map<String, ?> params) {
        io.restassured.response.Response response = null;
        try {
            long start = System.currentTimeMillis();
            response = RestAssured.given()
                    .baseUri(baseUri)
                    .params(params)
                    .headers(headers)
                    .contentType(io.restassured.http.ContentType.JSON)
                    .expect()
                    .when().get(endpoint);

            long end = System.currentTimeMillis();
            String url = baseUri + endpoint;

            Reporting.getLogger().log(LogStatus.INFO, "Value of response code for endpoint " + endpoint + " is " + response.statusCode());
            Reporting.getLogger().log(LogStatus.INFO, "Response is "
                    + "\"" + response.prettyPrint() + "\"");

            if (response != null) {
                Reporting.getLogger().logApiResponseTime(LogStatus.PASS, "Successful hit to Users API Endpoint : " + url, (end - start) / 1000 + " secs");

            } else {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to access Users API Endpoint : " + url);

            }

            return response;

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while getting response from url : " +
                    "" + baseUri + endpoint);

            return null;
        }
    }

    /**
     * Post URL with Encoded format:-
     *
     * @param baseUri
     * @param endpoint
     * @param grantType
     * @param headers
     * @return
     */
    public io.restassured.response.Response postRequestUrlEncoded(String baseUri,
                                                                  String endpoint,
                                                                  String grantType,
                                                                  Map<String, ?> headers) {
        io.restassured.response.Response response = null;
        try {
            long start = System.currentTimeMillis();


            response = RestAssured.given()
                    .baseUri(baseUri)
                    .headers(headers)
                    .contentType(io.restassured.http.ContentType.URLENC.withCharset("UTF-8"))
                    .formParam("grant_type", grantType)
                    .post(endpoint);

            long end = System.currentTimeMillis();
            String url = baseUri + endpoint;

            Reporting.getLogger().log(LogStatus.INFO, "Value of response code for endpoint " + endpoint + " is " + response.statusCode());
            Reporting.getLogger().log(LogStatus.INFO, "Response is "
                    + "\"" + response.prettyPrint() + "\"");

            if (response != null) {
                Reporting.getLogger().logApiResponseTime(LogStatus.PASS, "Successful hit to Users API Endpoint : " + baseUri + endpoint, (end - start) / 1000 + " secs");

            } else {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to access Users API Endpoint : " + url);

            }

            return response;

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while getting response from url : " +
                    "" + baseUri + endpoint);

            return null;
        }
    }


    /**
     * Post URL with body in json format:-
     *
     * @param baseUri
     * @param endpoint
     * @param body
     * @param headers
     * @return
     */
    public io.restassured.response.Response postRequest(String baseUri,
                                                        String endpoint,
                                                        String body,
                                                        Map<String, ?> headers) {
        io.restassured.response.Response response;
        try {
            long start = System.currentTimeMillis();


            response = RestAssured.given()
                    .headers(headers)
                    .baseUri(baseUri)
                    .body(body)
                    .when()
                    .post(endpoint);

            Reporting.getLogger().log(LogStatus.INFO, "Value of response code for endpoint " + endpoint + " is " + response.statusCode());
            Reporting.getLogger().log(LogStatus.INFO, "Response is "
                    + "\"" + response.prettyPrint() + "\"");
            long end = System.currentTimeMillis();
            String url = baseUri + endpoint;

            if (response != null) {
                Reporting.getLogger().logApiResponseTime(LogStatus.PASS,
                        "Successful hit to Users API Endpoint : "
                                + baseUri + endpoint, (end - start) / 1000 + " secs");

            } else {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to access Users API Endpoint : " + url);

            }


            return response;

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while getting response from url : " +
                    "" + baseUri + endpoint);
            return null;
        }
    }

}
