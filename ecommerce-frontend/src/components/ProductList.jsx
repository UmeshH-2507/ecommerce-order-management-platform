import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { fetchProducts } from '../store/productSlice'
import { addToCart } from '../store/cartSlice'
import { Link, useNavigate } from 'react-router-dom'

function ProductList() { 
  const dispatch = useDispatch() 
  const navigate = useNavigate() 

 const [selectedImage, setSelectedImage] = useState(null)

  const { products, loading, error } = useSelector( 
    state => state.products 
  ) 

  const cartError = useSelector(
  state => state.cart.error
)
 
  useEffect(() => { 
    dispatch(fetchProducts()) 
  }, [dispatch]) 

  const handleAddToCart = (product) => {

    const token = localStorage.getItem('token')

    if (!token) {
      navigate('/login')
      return
    }

    dispatch(addToCart(product))
  }
 
  if (loading) return <h2>Loading products...</h2> 
 
  if (error) return <h2>Error: {error}</h2>

 return (
  <div className="page">

    {cartError && (
  <p className="error-message">
    {cartError}
  </p>
)}

    <div className="page-header">
      <h1>Products</h1>

      <Link to="/cart">
        Go to Cart
      </Link>
    </div>

    <div className="product-grid">

      {products.map(product => (

        <div className="product-card" key={product.id}>

         {product.imageUrl && (
  <img
    src={product.imageUrl}
    alt={product.name}
    className="product-image"
    onClick={() => setSelectedImage(product.imageUrl)}
  />
)}

          <h2>{product.name}</h2>

          <p className="product-price">
            ₹{product.price}
          </p>

          <p>{product.description}</p>

          <p className="product-category">
            {product.category}
          </p>

          <button
          onClick={() => handleAddToCart(product)}
          >
          Add to Cart
          </button>

        </div>

      ))}

    </div>

    {selectedImage && (
      <div
        className="image-modal"
        onClick={() => setSelectedImage(null)}
      >
        <img
          src={selectedImage}
          alt="Product preview"
          className="zoomed-image"
          onClick={(e) => e.stopPropagation()}
        />
      </div>
    )}
    
  </div>
)
}

export default ProductList