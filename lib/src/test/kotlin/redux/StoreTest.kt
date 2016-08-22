package redux

import org.jetbrains.spek.api.Spek
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import redux.Store.Subscriber
import redux.helpers.ActionCreators.addTodo
import redux.helpers.ActionCreators.unknownAction
import redux.helpers.Reducers
import redux.helpers.Todos
import redux.helpers.Todos.State
import redux.helpers.Todos.Todo
import kotlin.test.expect

/*
 * Copyright (C) 2016 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class StoreTest : Spek({

    describe("Store") {

        describe("create") {

            it("passes the initial action and the initial state") {
                val store = Store.create(Reducers.TODOS, State(
                        listOf(
                                Todo(1, "Hello")
                        )
                ))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello")
                            )
                    )
                }
            }

            it("applies the reducer to the previous state") {
                val store = Store.create(Reducers.TODOS, State())
                expect(store.getState()) { Todos.State() }

                store.dispatch(unknownAction())
                expect(store.getState()) { Todos.State() }

                store.dispatch(addTodo("Hello"))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello")
                            )
                    )
                }

                store.dispatch(addTodo("World"))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello"),
                                    Todo(2, "World")
                            )
                    )
                }
            }

            it("applies the reducer to the initial state") {
                val store = Store.create(Reducers.TODOS, State(
                        listOf(
                                Todo(1, "Hello")
                        )
                ))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello")
                            )
                    )
                }

                store.dispatch(unknownAction())
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello")
                            )
                    )
                }

                store.dispatch(addTodo("World"))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello"),
                                    Todo(2, "World")
                            )
                    )
                }

            }

            it("preserves the state when replacing a reducer") {
                val store = Store.create(Reducers.TODOS, State())
                store.dispatch(addTodo("Hello"))
                store.dispatch(addTodo("World"))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello"),
                                    Todo(2, "World")
                            )
                    )
                }

                store.replaceReducer(Reducers.TODOS_REVERSE)
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(1, "Hello"),
                                    Todo(2, "World")
                            )
                    )
                }

                store.dispatch(addTodo("Perhaps"))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(3, "Perhaps"),
                                    Todo(1, "Hello"),
                                    Todo(2, "World")
                            )
                    )
                }

                store.replaceReducer(Reducers.TODOS)
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(3, "Perhaps"),
                                    Todo(1, "Hello"),
                                    Todo(2, "World")
                            )
                    )
                }

                store.dispatch(addTodo("Surely"))
                expect(store.getState()) {
                    Todos.State(
                            listOf(
                                    Todo(3, "Perhaps"),
                                    Todo(1, "Hello"),
                                    Todo(2, "World"),
                                    Todo(4, "Surely")
                            )
                    )
                }

            }

            it("supports multiple subscriptions") {
                val store = Store.create(Reducers.TODOS, State())
                val subscriberA = mock(Subscriber::class.java)
                val subscriberB = mock(Subscriber::class.java)

                val subscriptionA = store.subscribe(subscriberA)
                store.dispatch(unknownAction())
                verify(subscriberA, times(1)).onStateChanged()
                verify(subscriberB, times(0)).onStateChanged()

                store.dispatch(unknownAction())
                verify(subscriberA, times(2)).onStateChanged()
                verify(subscriberB, times(0)).onStateChanged()

                val subscriptionB = store.subscribe(subscriberB)
                store.dispatch(unknownAction())
                verify(subscriberA, times(3)).onStateChanged()
                verify(subscriberB, times(1)).onStateChanged()

                subscriptionA.unsubscribe()
                verify(subscriberA, times(3)).onStateChanged()
                verify(subscriberB, times(1)).onStateChanged()

                store.dispatch(unknownAction())
                verify(subscriberA, times(3)).onStateChanged()
                verify(subscriberB, times(2)).onStateChanged()

                subscriptionB.unsubscribe()
                verify(subscriberA, times(3)).onStateChanged()
                verify(subscriberB, times(2)).onStateChanged()

                store.dispatch(unknownAction())
                verify(subscriberA, times(3)).onStateChanged()
                verify(subscriberB, times(2)).onStateChanged()

                val subscriptionA2 = store.subscribe(subscriberA)
                verify(subscriberA, times(3)).onStateChanged()
                verify(subscriberB, times(2)).onStateChanged()

                store.dispatch(unknownAction())
                verify(subscriberA, times(4)).onStateChanged()
                verify(subscriberB, times(2)).onStateChanged()
            }

            it("only removes listener once when unsubscribe is called") {
                val store = Store.create(Reducers.TODOS, State())
                val subscriberA = mock(Subscriber::class.java)
                val subscriberB = mock(Subscriber::class.java)

                val subscriptionA = store.subscribe(subscriberA)
                store.subscribe(subscriberB)

                subscriptionA.unsubscribe()
                subscriptionA.unsubscribe()

                store.dispatch(unknownAction())
                verify(subscriberA, times(0)).onStateChanged()
                verify(subscriberB, times(1)).onStateChanged()
            }

        }

    }

})
