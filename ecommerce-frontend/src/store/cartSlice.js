import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import axiosInstance from '../api/axiosInstance'

// Fetch cart
export const fetchCart = createAsyncThunk(
  'cart/fetchCart',
  async () => {
    const response = await axiosInstance.get('/api/cart')
    return response.data
  }
)

// Add item to cart
export const addToCart = createAsyncThunk(
  'cart/addToCart',
  async (product, { rejectWithValue }) => {

    try {

      const response = await axiosInstance.post(
        '/api/cart/items',
        {
          productId: product.id,
          quantity: 1
        }
      )

      return response.data

    } catch (error) {

      return rejectWithValue(
        error.response?.data?.message ||
        error.response?.data ||
        'Failed to add product to cart'
      )
    }
  }
)

// Remove item from cart
export const removeCartItem = createAsyncThunk(
  'cart/removeCartItem',
  async (productId) => {
    await axiosInstance.delete(`/api/cart/items/${productId}`)

    return productId
  }
)

export const updateCartQuantity = createAsyncThunk(
  'cart/updateCartQuantity',
  async ({ productId, quantity }) => {
    await axiosInstance.put(
      `/api/cart/items/${productId}`,
      null,
      {
        params: {
          quantity
        }
      }
    )

    return {
      productId,
      quantity
    }
  }
)

const initialState = {
  items: [],
  loading: false,
  error: null
}

const cartSlice = createSlice({
  name: 'cart',

  initialState,

  reducers: {
    clearCart: (state) => {
      state.items = []
    }
  },

  extraReducers: (builder) => {
    builder

      // =========================
      // FETCH CART
      // =========================

      .addCase(fetchCart.pending, (state) => {
        state.loading = true
        state.error = null
      })

      .addCase(fetchCart.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
      })

      .addCase(fetchCart.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })

      // =========================
      // ADD TO CART
      // =========================

      .addCase(addToCart.pending, (state) => {
        state.loading = true
        state.error = null
      })

      .addCase(addToCart.fulfilled, (state, action) => {
        state.loading = false

        const addedItem = action.payload

        const existingItem = state.items.find(
          item => item.productId === addedItem.productId
        )

        if (existingItem) {
          existingItem.quantity = addedItem.quantity
          existingItem.price = addedItem.price
        } else {
          state.items.push(addedItem)
        }
      })

      .addCase(addToCart.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })

      // =========================
      // REMOVE FROM CART
      // =========================

      .addCase(removeCartItem.pending, (state) => {
        state.loading = true
        state.error = null
      })

      .addCase(removeCartItem.fulfilled, (state, action) => {
        state.loading = false

        state.items = state.items.filter(
          item => item.productId !== action.payload
        )
      })

      .addCase(removeCartItem.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })


.addCase(updateCartQuantity.pending, (state) => {
  state.loading = true
  state.error = null
})
.addCase(updateCartQuantity.fulfilled, (state, action) => {
  state.loading = false

  const item = state.items.find(
    item => item.productId === action.payload.productId
  )

  if (item) {
    item.quantity = action.payload.quantity
  }
})
.addCase(updateCartQuantity.rejected, (state, action) => {
  state.loading = false
  state.error = action.error.message
})

  }
})

export const { clearCart } = cartSlice.actions

export default cartSlice.reducer