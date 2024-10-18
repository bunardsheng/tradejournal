// next.config.mjs

/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
      return [
        {
          source: '/api/:path*',
          destination: 'http://localhost:8080/api/:path*' // Adjust if your Spring Boot app runs on a different port
        }
      ]
    }
  }
  
  export default nextConfig;