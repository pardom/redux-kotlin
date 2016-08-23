package redux

import org.jetbrains.spek.api.Spek
import redux.helpers.Reducers
import redux.helpers.Todos.Action.AddTodo
import redux.helpers.Todos.State
import redux.helpers.Todos.Todo
import kotlin.test.assertEquals

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

class MiddlewareTest : Spek({

    describe("Middleware") {

        describe("apply") {

            it("wraps dispatch method with middleware once") {

                var dispatches = 0
                val middleware = { store: Store<State>, action: Any, next: Dispatcher ->
                    val result = next.dispatch(action)
                    dispatches++
                    action
                }

                val initialState = State(listOf(Todo(1, "Hello")))
                val store = Store.create(Reducers.TODOS, initialState, Middleware.apply(middleware))

                assertEquals(1, dispatches)

                store.dispatch(AddTodo("World"))

                assertEquals(2, dispatches)

            }

        }

    }

})
