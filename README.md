# Android-Event-Finder-Application
This application parses through JSON Data given by the ticketmaster API by allowing users to search for events
(keywords) and location (city). The application then displays search results in a recyclerview. Each event is listed
as a card or list item (event name, venue, date, time, ticket price range, etc.). The app allows users to click
a button called "see tickets" which opens the web browser (in this case google chrome) with the ticket master link.
Users can mark events as favorites, which are stored in Firestore, and view their favorite events by clicking a button
called "favorites". On each ticket, they can also access event descriptions by clicking "event description" button,
which generates event descriptions from Gemini API. The users are also greeted by Firebase Authentication, which allows
the users to login/sign up.
