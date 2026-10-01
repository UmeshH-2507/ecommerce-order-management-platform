import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../store/authSlice'

function Login() {

  const dispatch = useDispatch()
  const navigate = useNavigate()

  const { loading, error } = useSelector(
    state => state.auth
  )

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const handleSubmit = (e) => {

    e.preventDefault()

    dispatch(login({
      email,
      password
    }))
      .unwrap()
      .then(() => {
        navigate('/')
      })
  }

  return (
    <div className="page">

      <div className="auth-container">

        <h1>Login</h1>

        <form
          className="auth-form"
          onSubmit={handleSubmit}
        >

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button
            type="submit"
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>

        </form>

        <p className="auth-link">
          Don't have an account?{' '}
          <Link to="/register">
            Register
          </Link>
        </p>

        {error && (
          <p className="error-message">
            {error.message || error}
          </p>
        )}

      </div>

    </div>
  )
}

export default Login