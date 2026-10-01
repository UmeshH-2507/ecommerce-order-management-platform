import { useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { Link } from 'react-router-dom'
import {
  fetchCart,
  removeCartItem,
  updateCartQuantity
} from '../store/cartSlice'

function Cart() {

  const dispatch = useDispatch()

  const { items, loading, error } = useSelector(
    state => state.cart
  )

  const { products } = useSelector(
    state => state.products
  )

  useEffect(() => {
    dispatch(fetchCart())
  }, [dispatch])

  const total = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  )

  const handleIncrease = (item) => {
    dispatch(
      updateCartQuantity({
        productId: item.productId,
        quantity: item.quantity + 1
      })
    )
  }

  const handleDecrease = (item) => {

    if (item.quantity > 1) {
      dispatch(
        updateCartQuantity({
          productId: item.productId,
          quantity: item.quantity - 1
        })
      )
    }
  }

  if (loading) {
    return <h2>Loading cart...</h2>
  }

  if (error) {
    return <h2>Error: {error}</h2>
  }

  return (
    <div className="page">

      <div className="page-header">

        <h1>Shopping Cart</h1>

        {items.length > 0 && (
  <Link to="/">
    Continue Shopping
  </Link>
)}

      </div>

      {items.length === 0 ? (

        <div className="empty-cart">
          <h2>Your cart is empty</h2>

          <Link to="/">
            Start Shopping
          </Link>
        </div>

      ) : (

        <>

          <div className="cart-list">

            {items.map(item => {

              const product = products.find(
                product => product.id === item.productId
              )

              return (
                <div
                  className="cart-item"
                  key={item.productId}
                >

                  <div className="cart-product">

                    {product?.imageUrl && (
                      <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="cart-image"
                      />
                    )}

                    <div>

                      <h3>
                        {product?.name ||
                          `Product ${item.productId}`}
                      </h3>

                      <p>
                        Price: ₹{item.price}
                      </p>

                    </div>

                  </div>

                  <div className="cart-quantity">

                    <button
                      onClick={() => handleDecrease(item)}
                      disabled={item.quantity === 1}
                    >
                      −
                    </button>

                    <span>
                      {item.quantity}
                    </span>

                    <button
                      onClick={() => handleIncrease(item)}
                    >
                      +
                    </button>

                  </div>

                  <div className="cart-subtotal">

                    <p>
                      Subtotal
                    </p>

                    <strong>
                      ₹{item.price * item.quantity}
                    </strong>

                  </div>

                  <button
                    onClick={() =>
                      dispatch(
                        removeCartItem(item.productId)
                      )
                    }
                  >
                    Remove
                  </button>

                </div>
              )
            })}

          </div>

          <div className="cart-summary">

            <h2>
              Total: ₹{total}
            </h2>

            <Link
              className="checkout-button"
              to="/checkout"
            >
              Proceed to Checkout
            </Link>

          </div>

        </>

      )}

    </div>
  )
}

export default Cart