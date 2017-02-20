# om-mutation


## Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(run)
(browser-repl)
```

The call to `(run)` starts the Figwheel server at port 3449, which takes care of
live reloading ClojureScript code and CSS. Figwheel's server will also act as
your app server, so requests are correctly forwarded to the http-handler you
define.

Running `(browser-repl)` starts the Figwheel ClojureScript REPL. Evaluating
expressions here will only work once you've loaded the page, so the browser can
connect to Figwheel.

When you see the line `Successfully compiled "resources/public/app.js" in 21.36
seconds.`, you're ready to go. Browse to `http://localhost:3449` and enjoy.

**Attention: It is not needed to run `lein figwheel` separately. Instead we
launch Figwheel directly from the REPL**

## Testing Mutation-invoked renders

This sample application was used to test what components get re-rendered following a `transact!`, with and without follow-on reads.

Each component will output debug to the browser console when being rendered, so look at the console to see what transactions result in which renders.

The cases tested by the application include:

a) In a transaction with only a mutation (eg `'[(child1-child2/inc)]`)
  * the component referenced by the transaction will be rendered, along with any of its children whose properties have been changed.
  * if the component referenced has an ident, then any other component with the same ident will be re-rendered
  * Click on `Increment` for "Child1" to see an example of this.

b) In a transaction with a mutation and follow-on read of an ident (eg `' [(child4-sibling-Chris/inc) [:sibling/by-name "Chris"]]`)
  * as above for a)
  * in addition, any component with the ident in the follow-on read will be rendered, along with any of its children whose properties have been changed.
  * click on the `Increment` for any of the "Sibling" components to see an example of this.
  * Note: "Sibling - Harry" appears twice, so clicking on any of these will update/render the other.

c) In a transaction with a mutation and a follow-on read of a keyword (eg `' [(child1-child2/inc) :child1/name]`)
  * the Root component will be rendered, along with any children in the component tree whose properties have been modified.
  * click on the `Increment` for "Child2" or "Child3" or "Child5" to see an example of this.



Although c) works fine, I was expecting the same optimizations as in a) and b) in that the rendering would only be performed on the components explicitly referenced by the transaction and follow-on reads.

## Example change to om/next.cljc

I tried a quick change to om/next.cljc locally to see whether the case of c) could be changed to just update those components referenced by the mutation.

These changes were all in `om/next.cljc`


Add an optional argument to `transact*` called `orig-tx`

```
 (defn transact*
   ([r c ref tx] (transact* r c ref tx tx))
   ([r c ref tx orig-tx]
```


In `transact*` use this to queue follow on read keys instead of keys of `v`, the parser result.

``` abap
    ;;;(p/queue! r (into q (remove symbol?) (keys v)))
       (p/queue! r (into q (remove symbol?) orig-tx))
```


In `transact!` include original `tx` along with `transformed-reads`

```
 (transact* r x nil transformed-reads tx))

```





With these changes, clicking `Increment` for the following components results in:

* Child2: only rendered Child2 (component of the transaction) and Child1 (component that queries `:child1/name`)
* Child3: only rendered Child3 (component of the transaction) and Child1 (component that queries `:child1/name`)
* Child5: only rendered Child1 and Child2 (components that query `:common\age`) and Child3 (component that queires `:child3\name`)

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

## Chestnut

Created with [Chestnut](http://plexus.github.io/chestnut/) 0.14.0 (66af6f40).
