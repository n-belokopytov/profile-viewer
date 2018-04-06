# profile-viewer
A profile viewer Android app using MVVM and LiveData, Dagger2, RXJava2, Mockito, Picasso and other, smaller libs. 

## Project Structure
This app has been written in Kotlin and devided into Data, Domain and UI layers, the last one using MVVM approach.
### Configuration
There is a makeshift configuration file that supports changing the host url for endpoints and enable mock API responses. It's located in .data.common.NetworkConfig object

### Dependency Injection
  There is a DI folder that contains Dagger2 bindings and is devided into ActivitiesModule and a more general AppModule
  ### Domain
  Stores usecases that contain business logic, are instantiated with Repositories and output LiveData
  ### Data
  Contains Repositories that use different Data Sources to provide the rest of the app with Reactive interfaces with Models. UserRepo supports offline caching, Session tokens are not being stored, since it's assumed that a new Session will require a new token.
  Also contains a NetworkConfig object that is used to manipulate Host Urls, Fake vs Real responses and holds the current token. 
  ### UI
  Contains Activities that act as Views, ViewModel classes and objects that manage states of UI that are reffered to as Models of this layer.
  
 ## Tests
 I had time to write only one decent test, that is focused on the most critical part of the app IMO - Login use case. It uses Mockito library to mock dependencies.
 
 ## General thoughts and learnings
 - The requirement to store password offline and a lack of userid in the response from the server presented an interesting challenge and was a personal high point for me.
 
  - I have discovered https://my-json-server.typicode.com/ - a git-baseed mock-server. It is a great tool that I will be using more in the future. Unfortunately it does not support customizable POST responses.
 
 - I would not have stored the password in any way shape or form locally in production environment, using more of an OAUTH2 approach with expireable tokens and I would not send it over the network in plaintext. I would have it transferred in a form of a salted hash.
 
 - I would say that the requirements to make a tested, clean, well decoupled solution with 2 screens and 3 endpoints was something that was quite challenging to do in the allocated time and it made me think hard about prioritisation.
 
  ## Top 3 things missing
 - Tests that cover the other use cases and high-level repo operations
 - "Local" build target that uses Mockito to mock responses from Network data sources
 - Error handling leaves much to be desired
 
