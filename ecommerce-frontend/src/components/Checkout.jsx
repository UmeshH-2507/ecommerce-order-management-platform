import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { createOrder } from '../store/orderSlice'
import { clearCart } from '../store/cartSlice'
import { Link } from 'react-router-dom'

function Checkout({ onOrderCreated }) {

  const dispatch = useDispatch()

  const { items } = useSelector(
    state => state.cart
  )

  const { loading, error, order } = useSelector(
    state => state.order
  )

  const total = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  )

  const [shippingAddress, setShippingAddress] = useState({
    fullName: '',
    phone: '',
    addressLine: '',
    city: '',
    state: '',
    postalCode: '',
    country: ''
  })

  const handleChange = (e) => {

    const { name, value } = e.target

    setShippingAddress({
      ...shippingAddress,
      [name]: value
    })
  }

  const handlePlaceOrder = (e) => {

    e.preventDefault()

    const orderData = {
      cartId: 4,
      shippingAddress: shippingAddress,
      simulatePaymentFailure: false
    }

    dispatch(createOrder(orderData))
      .unwrap()
      .then(() => {

        dispatch(clearCart())

        setShippingAddress({
          fullName: '',
          phone: '',
          addressLine: '',
          city: '',
          state: '',
          postalCode: '',
          country: ''
        })

        onOrderCreated()
      })
  }

  return (
    <div className="page">

      <div className="page-header">

        <h1>Checkout</h1>

        <Link to="/cart">
          Back to Cart
        </Link>

      </div>

      <div className="checkout-container">

        <div className="checkout-summary">

          <h2>Order Summary</h2>

          {items.map(item => (

            <div
              className="checkout-item"
              key={item.productId}
            >

              <div>
                <p>
                  Product ID: {item.productId}
                </p>

                <p>
                  Quantity: {item.quantity}
                </p>
              </div>

              <strong>
                ₹{item.price * item.quantity}
              </strong>

            </div>

          ))}

          <div className="checkout-total">
            <h2>
              Total: ₹{total}
            </h2>
          </div>

        </div>

        <div className="checkout-form-container">

          <h2>Shipping Address</h2>

          <form onSubmit={handlePlaceOrder}>

            <input
              type="text"
              name="fullName"
              placeholder="Full Name"
              value={shippingAddress.fullName}
              onChange={handleChange}
              required
            />

            <input
              type="text"
              name="phone"
              placeholder="Phone"
              value={shippingAddress.phone}
              onChange={handleChange}
              required
            />

            <input
              type="text"
              name="addressLine"
              placeholder="Address"
              value={shippingAddress.addressLine}
              onChange={handleChange}
              required
            />

            <input
              type="text"
              name="city"
              placeholder="City"
              value={shippingAddress.city}
              onChange={handleChange}
              required
            />

            <input
              type="text"
              name="state"
              placeholder="State"
              value={shippingAddress.state}
              onChange={handleChange}
              required
            />

            <input
              type="text"
              name="postalCode"
              placeholder="Postal Code"
              value={shippingAddress.postalCode}
              onChange={handleChange}
              required
            />

            <input
              type="text"
              name="country"
              placeholder="Country"
              value={shippingAddress.country}
              onChange={handleChange}
              required
            />

            <button
              type="submit"
              disabled={loading || items.length === 0}
            >
              {loading
                ? 'Placing Order...'
                : 'Place Order'}
            </button>

          </form>

          {error && (
            <p className="error-message">
              Error: {error.message || error}
            </p>
          )}

          {order && (
            <div className="success-message">

              <h2>
                Order Created Successfully!
              </h2>

              <p>
                Order ID: {order.orderId}
              </p>

              <p>
                Status: {order.status}
              </p>

              <p>
                Total: ₹{order.totalAmount}
              </p>

              <Link to={`/orders/${order.orderId}`}>
                View Order Details
              </Link>

            </div>
          )}

        </div>

      </div>

    </div>
  )
}

export default Checkout