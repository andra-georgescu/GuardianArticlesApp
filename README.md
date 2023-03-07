This is a simple app showcasing some Android development and testing best practices. It is a work in progress and contributions are welcome.

The app uses The Guardian's public api, which you can find more documentation on [here](https://open-platform.theguardian.com/documentation/)

Some of the used libraries:
- coroutines
- Room
- Retrofit
- Hilt

In order for the app to work, you will need an API key. You can get it by registering [here](https://bonobo.capi.gutools.co.uk/register/developer), and then adding your key to api_constants.xml

Current planned improvements:
- [ ] search feature so users can read on a topic of interest
- [ ] pagination and infinite loading of the articles list
- [ ] supporting links in the article details screen
- [ ] migrating the UI to Compose
