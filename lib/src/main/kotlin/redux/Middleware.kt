package redux

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

interface Middleware<S : Any> {

    fun dispatch(store: Store<S>, action: Any, next: Dispatcher): Any

    private class Enhancer<S : Any>(val middlewares: Array<out Middleware<S>>) : Store.Enhancer<S> {

        override fun enhance(next: Store.Creator<S>): Store.Creator<S> = Creator(next, middlewares)

    }

    private class Creator<S : Any>(
            val creator: Store.Creator<S>,
            val middlewares: Array<out Middleware<S>>) : Store.Creator<S> {

        override fun create(
                reducer: Reducer<S>,
                initialState: S,
                enhancer: Store.Enhancer<S>?): Store<S> {

            return Delegate(creator.create(reducer, initialState, enhancer), middlewares)

        }

    }

    private class Delegate<S : Any>(
            store: Store<S>,
            middlewares: Array<out Middleware<S>>) : Store<S> by store {

        val rootDispatcher = middlewares.foldRight(store as Dispatcher) { middleware, next ->
            Wrapper(middleware, store, next)
        }

        override fun dispatch(action: Any): Any {
            return rootDispatcher.dispatch(action)
        }

        class Wrapper<S : Any>(
                val middleware: Middleware<S>,
                val store: Store<S>,
                val next: Dispatcher) : Dispatcher {

            override fun dispatch(action: Any): Any {
                return middleware.dispatch(store, action, next)
            }

        }

    }

    companion object {

        fun <S : Any> apply(vararg middlewares: Middleware<S>): Store.Enhancer<S> {
            return Enhancer(middlewares)
        }

    }

}
