import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import orderAxios from '../api/orderAxios'

function OrderDetails() {

  const { orderId } = useParams()

  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {

    const fetchOrder = async () => {

      try {

        const response = await orderAxios.get(
          `/api/orders/${orderId}`
        )

        setOrder(response.data)

      } catch (error) {

        setError(
          error.response?.data?.message ||
          'Failed to fetch order'
        )

      } finally {

        setLoading(false)

      }
    }

    fetchOrder()

  }, [orderId])

  if (loading) {
    return <h2>Loading order...</h2>
  }

  if (error) {
    return <h2>Error: {error}</h2>
  }

  return (
    <div className="page">

      <div className="page-header">

        <h1>Order Details</h1>

        <Link to="/orders">
          Back to My Orders
        </Link>

      </div>

      <div className="order-details-card">

        <div className="order-details-header">

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

      </div>

      <div className="details-section">

        <h2>Shipping Address</h2>

        <div className="address-card">

          <p>
            <strong>{order.shippingAddress.fullName}</strong>
          </p>

          <p>
            {order.shippingAddress.phone}
          </p>

          <p>
            {order.shippingAddress.addressLine}
          </p>

          <p>
            {order.shippingAddress.city},{' '}
            {order.shippingAddress.state}
          </p>

          <p>
            {order.shippingAddress.postalCode},{' '}
            {order.shippingAddress.country}
          </p>

        </div>

      </div>

      <div className="details-section">

        <h2>Items</h2>

        <div className="order-items">

          {order.items.map(item => (

            <div
              className="order-item"
              key={item.productId}
            >

              <div>

                <h3>
                  Product ID: {item.productId}
                </h3>

                <p>
                  Quantity: {item.quantity}
                </p>

              </div>

              <strong>
                ₹{item.price * item.quantity}
              </strong>

            </div>

          ))}

        </div>

      </div>

    </div>
  )
}

export default OrderDetails