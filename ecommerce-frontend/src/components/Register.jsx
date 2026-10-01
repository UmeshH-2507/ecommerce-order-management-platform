import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'

function Register() {

  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleSubmit = async (e) => {

    e.preventDefault()

    try {

      setLoading(true)
      setError(null)

      await axios.post(
        'http://localhost:8080/api/users/register',
        {
          name,
          email,
          password
        }
      )

      navigate('/login')

    } catch (error) {

      setError(
        error.response?.data?.message ||
        error.response?.data ||
        'Registration failed'
      )

    } finally {

      setLoading(false)

    }
  }

  return (
    <div className="page">

      <div className="auth-container">

        <h1>Register</h1>

        <form
          className="auth-form"
          onSubmit={handleSubmit}
        >

          <input
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />

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
            {loading ? 'Registering...' : 'Register'}
          </button>

        </form>

        <p className="auth-link">
          Already have an account?{' '}
          <Link to="/login">
            Login
          </Link>
        </p>

        {error && (
          <p className="error-message">
            {error}
          </p>
        )}

      </div>

    </div>
  )
}

export default Register