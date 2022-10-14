# Pantheon IoC Server

Results of a simple experiment to create a library that provides fast REST server setup with minimum impact on your code
style. Full frameworks tend to force a particular way of writing and testing code and IoC Server aims to avoid that as
much as possible.

The experiment escalated a bit so now there is a set of tools created around using this library under the name
of [Pantheon](https://github.com/asutalo/pantheon).

At this point not recommended being taken seriously and used in anything other than experiments where you need a fast
start.

## Usage

Essentially all you have to do is define your endpoints by extending the Endpoint class and register by calling the
`register` method on the Server. Afterwards you simply instantiate the Server and run it in your Main class. At that
point the Server takes over and routes incoming requests to the appropriate registered Endpoint.

An Endpoint is registered by specifying a Uri definition that describes your expected parameters using Regex. The Uri
definition requires you to define names for all the parameters in 2 ways depending on them being path or query params.

For example: `/hello/(id=\d{2})\?name=(\w*)` defines an Endpoint at `/hello/` followed by a path param *id* which has to
be a **double digit**, and a query parameter, *name* which has to be a **word**. In case the params are provided in an
incorrect format the request will come back as a ***404***.

After you extend Endpoints class you have a choice of overriding the default methods for:
* get
* post
* put
* delete
* head
* patch

Overriding these will allow your Endpoint to process the corresponding requests, should a request come using a method 
which is not implemented by you, it will respond with ***501***.

Each of the methods above responds with a *Response* object, and you can use the existing *SimpleResponse* class or
create your own implementation as appropriate.

In case you need to respond with errors, the Server contains an IocServerException class you can extend in any of your
custom exceptions and throw them instead of the Response object from your endpoints.

Additionally, the Endpoint methods will provide you with a Map of Uri params, the body (which has to come in ***json***
format), and Headers. The headers are an Immutable Map however in case you need to add headers to your Response, you can
do so by overriding the `getHeaders` method. The Server will add your custom headers to the response.

## Sample implementation

````
class Sample {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8080, 150, 50, 10, TimeUnit.SECONDS, true);

        server.registerEndpoint(new HelloWorld("/helloWorld"));
        
        server.start();
    }

    static class HelloWorld extends Endpoint{
        public HelloWorld(String uriDefinition) {
            super(uriDefinition);
        }

        @Override
        public Response get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            return new SimpleResponse(200, "Hello World!");
        }
    }
}
````

When the main method is executed navigate to `http://localhost:8080/helloWorld` and say Hello to the world!

## Importing

The library is available via [![](https://jitpack.io/v/asutalo/pantheon-ioc-server.svg)](https://jitpack.io/#asutalo/pantheon-ioc-server)
