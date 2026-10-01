import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import axios from 'axios'

export const login = createAsyncThunk(
  'auth/login',
  async (loginData, { rejectWithValue }) => {
    try {
      const response = await axios.post(
        'http://localhost:8080/api/users/login',
        loginData
      )

      return response.data
    } catch (error) {
      return rejectWithValue(
        error.response?.data || 'Login failed'
      )
    }
  }
)

const initialState = {
  token: localStorage.getItem('token'),
  loading: false,
  error: null
}

const authSlice = createSlice({
  name: 'auth',
  initialState,

  reducers: {
    logout: (state) => {
      state.token = null
      localStorage.removeItem('token')
    }
  },

  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true
        state.error = null
      })

      .addCase(login.fulfilled, (state, action) => {
        state.loading = false
        state.token = action.payload.token

        localStorage.setItem(
          'token',
          action.payload.token
        )
      })

      .addCase(login.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload
      })
  }
})

export const { logout } = authSlice.actions

export default authSlice.reducer