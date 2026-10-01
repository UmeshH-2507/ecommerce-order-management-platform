import './App.css'
import { useRef } from 'react'
import { Routes, Route, Link } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { logout } from './store/authSlice'

import Login from './components/Login'
import ProductList from './components/ProductList'
import Cart from './components/Cart'
import Checkout from './components/Checkout'
import OrderDetails from './components/OrderDetails'
import OrderHistory from './components/OrderHistory'
import ProtectedRoute from './components/ProtectedRoute'
import Register from './components/Register'

function App() {

 const dispatch = useDispatch()
  const orderHistoryRef = useRef(null)

  const { token } = useSelector(
    state => state.auth
  )

const { items } = useSelector(
  state => state.cart
)

const cartCount = items.reduce(
  (total, item) => total + item.quantity,
  0
)
  
  return (
    <>
      <nav className="navbar">

        <h2>E-Commerce</h2>

        <div className="nav-links">

          <Link to="/">
            Products
          </Link>

          <Link to="/cart">
            Cart ({cartCount})
          </Link>

          {token && (
            <Link to="/orders">
              My Orders
            </Link>
          )}

          {!token && (
            <Link to="/login">
              Login
            </Link>
          )}

          <button onClick={() => dispatch(logout())}>
           Logout
          </button>

        </div>

      </nav>

      <Routes>

        <Route
          path="/"
          element={<ProductList />}
        />

        <Route
  path="/cart"
  element={
    <ProtectedRoute>
      <Cart />
    </ProtectedRoute>
  }
/>

       <Route
  path="/checkout"
  element={
    <ProtectedRoute>
      <Checkout
        onOrderCreated={() =>
          orderHistoryRef.current?.refresh()
        }
      />
    </ProtectedRoute>
  }
/>

        <Route
  path="/orders"
  element={
    <ProtectedRoute>
      <OrderHistory ref={orderHistoryRef} />
    </ProtectedRoute>
  }
/>

        <Route
  path="/orders/:orderId"
  element={
    <ProtectedRoute>
      <OrderDetails />
    </ProtectedRoute>
  }
/>

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
  path="/register"
  element={<Register />}
/>

      </Routes>

    </>
  )
}

export default App