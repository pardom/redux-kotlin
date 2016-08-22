package redux.helpers

import redux.Reducer
import redux.helpers.Todos.Action
import redux.helpers.Todos.State
import redux.helpers.Todos.Todo

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

object Reducers {

    fun id(state: State): Int {
        return state.todos.fold(0) { result, item ->
            if (item.id > result) item.id else result
        } + 1
    }

    object TODOS : Reducer<State> {

        override fun reduce(state: State, action: Any): State {
            return when (action) {
                is Action.AddTodo -> state.copy(todos = state.todos + Todo(id(state), action.todo))
                else -> state
            }
        }

    }

    object TODOS_REVERSE : Reducer<State> {

        override fun reduce(state: State, action: Any): State {
            return when (action) {
                is Action.AddTodo -> state.copy(todos = listOf(Todo(id(state), action.todo)) + state.todos)
                else -> state
            }
        }

    }

}
