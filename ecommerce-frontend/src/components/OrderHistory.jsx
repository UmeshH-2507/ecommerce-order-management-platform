import {
  useEffect,
  useState,
  forwardRef,
  useImperativeHandle
} from 'react'

import { Link } from 'react-router-dom'
import orderAxios from '../api/orderAxios'

const OrderHistory = forwardRef((props, ref) => {

  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetchOrders = async () => {

    try {

      setLoading(true)

      const response = await orderAxios.get(
        '/api/orders/my-orders'
      )

      setOrders(response.data)
      setError(null)

    } catch (error) {

      setError(
        error.response?.data?.message ||
        'Failed to fetch orders'
      )

    } finally {

      setLoading(false)

    }
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  useImperativeHandle(ref, () => ({
    refresh: fetchOrders
  }))

  if (loading) {
    return <h2>Loading orders...</h2>
  }

  if (error) {
    return <h2>Error: {error}</h2>
  }

  if (orders.length === 0) {
    return (
      <div className="page">

        <h1>My Orders</h1>

        <p>No orders found</p>

        <Link to="/">
          Continue Shopping
        </Link>

      </div>
    )
  }

  return (
    <div className="page">

      <div className="page-header">

        <h1>My Orders</h1>

        <Link to="/">
          Continue Shopping
        </Link>

      </div>

      <div className="order-list">

        {orders.map(order => (

          <div
            className="order-card"
            key={order.orderId}
          >

            <div className="order-card-header">

              <h2>
                Order #{order.orderId}
              </h2>

              <span className="order-status">
                {order.status}
              </span>

            </div>

            <p>
              <strong>Total:</strong> ₹{order.totalAmount}
            </p>

            <p>
              <strong>Date:</strong> {order.createdAt}
            </p>

            <Link
              className="view-order-button"
              to={`/orders/${order.orderId}`}
            >
              View Details
            </Link>

          </div>

        ))}

      </div>

    </div>
  )
})

export default OrderHistory