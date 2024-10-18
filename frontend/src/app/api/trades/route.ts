import { NextRequest, NextResponse } from 'next/server';

const SPRING_BOOT_API = 'http://localhost:8080'; // Replace with your Spring Boot API URL

async function fetchFromSpringBoot(path: string, options: RequestInit = {}) {
  const res = await fetch(`${SPRING_BOOT_API}${path}`, {
    ...options,
    headers: {
      ...options.headers,
      'Content-Type': 'application/json',
    },
  });
  
  if (!res.ok) {
    throw new Error(`Spring Boot API responded with status: ${res.status}`);
  }
  
  return res.json();
}

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const path = searchParams.get('path') || '/api/trades/sorted-by-expiry';
  
  try {
    const data = await fetchFromSpringBoot(path);
    return NextResponse.json(data);
  } catch (error) {
    console.error('Error fetching from Spring Boot:', error);
    return NextResponse.json({ error: 'Failed to fetch data' }, { status: 500 });
  }
}

export async function POST(request: NextRequest) {
  const trades = await request.json();
  
  try {
    const result = await fetchFromSpringBoot('/api/trades/upload', {
      method: 'POST',
      body: JSON.stringify(trades),
    });
    return NextResponse.json(result);
  } catch (error) {
    console.error('Error posting to Spring Boot:', error);
    return NextResponse.json({ error: 'Failed to upload trades' }, { status: 500 });
  }
}