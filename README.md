I started this assessment knowing there were some Material3 Compose components I hadn't
played with yet, and the new Material3 SearchBar inside a Scaffold ended up being a great choice for the UI.

Overall the Flickr API wasn't too difficult to work with, and I aimed for separation of concerns via
domain layer mapping of the API response items at the repository layer.

What took up the most time on this project was working on my own pagination. I hadn't yet done that in Compose
(and I appreciate how we'd like to avoid Paging 3 :P), and I got a bit too in the weeds trying to figure out
how to work around the LazyGridColumn's seemingly buggy behavior in its derived list state when new items would 
get fetched. My hunch, in retrospect, is that it has something to do with the overall instability of my data
classes and lists, which I could have mitigate by wrapping lists in immutable holders and marking data classes as @Stable.

With more time I would have certainly unit tested the viewmodel at the very least (hence why the repository is an interface),
and done a detail screen when a thumbnail is clicked. I also would have implemented a SavedStateHandle
in the ViewModel for the text query. I thought about using Hilt, but for a small assessment project 
I don't really see DI as a concern since I can just pass default arguments and use a singleton for the
Retrofit client.

UI things I would have liked to do would be to have the SearchBar retract up/appear down alongside the 
LazyGridState as it gets scrolled downward/upward and put proper placeholders in the AsyncImage. 

Finally, I'm sure the viewmodel flow collection could be a bit cleaner, perhaps by having the repository
take care of some of the paging concerns.

Looking forward to discussing the code!

