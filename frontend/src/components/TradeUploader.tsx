'use client';

import React, { useState, useEffect } from 'react';
import Papa from 'papaparse';

interface Trade {
  activityDate: string;
  instrument: string;
  description: string;
  transactionCode: string;
  quantity: string;
  contractPrice: string;
  amount: string;
}

const TradeUploader: React.FC = () => {
  const [trades, setTrades] = useState<Trade[]>([]);
  const [optionType, setOptionType] = useState<string>('');
  const [instrument, setInstrument] = useState<string>('');
  const [transactionCode, setTransactionCode] = useState<string>('');

  useEffect(() => {
    fetchTrades();
  }, []);

  const fetchTrades = async () => {
    try {
      const response = await fetch('/api/trades/sorted-by-expiry');
      if (!response.ok) throw new Error('Failed to fetch trades');
      const data = await response.json();
      setTrades(data);
    } catch (error) {
      console.error('Error fetching trades:', error);
    }
  };

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;
    console.log("Selected File:", file);

    Papa.parse(file, {
      header: false,
      skipEmptyLines: true,
      complete: async (results) => {
        console.log("Finished:", results.data);
        const tradeObjects: Trade[] = [];

        // Loop through the parsed data
        for (let index = 1; index < results.data.length; index++) {
          const row = results.data[index] as string[];
          console.log(`Row ${index}:`, row);

          

          const tradeObject: Trade = {
            activityDate: row[0].trim(),
            instrument: row[3].trim(),
            description: row[4].trim(),
            transactionCode: row[5].trim(),
            quantity: row[6].trim(),
            contractPrice: row[7].trim(),
            amount: row[8].trim(),
          };

          tradeObjects.push(tradeObject);
        }

        // Upload all trade objects at once
        try {
          const response = await fetch('/api/trades/upload', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(tradeObjects),
          });

          if (!response.ok) throw new Error('Failed to upload trades');
          console.log("Trades uploaded successfully!");
          fetchTrades(); // Refresh trades after upload
        } catch (error) {
          console.error('Error uploading trades:', error);
        }
      },
    });
  };

  const fetchTradesByOptionType = async () => {
    try {
      const response = await fetch(`/api/trades/by-option-type/${optionType}`);
      if (!response.ok) throw new Error('Failed to fetch trades by option type');
      const data = await response.json();
      setTrades(data);
    } catch (error) {
      console.error('Error fetching trades by option type:', error);
    }
  };

  const fetchTradesByInstrument = async () => {
    try {
      const response = await fetch(`/api/trades/by-instrument/${instrument}`);
      if (!response.ok) throw new Error('Failed to fetch trades by instrument');
      const data = await response.json();
      setTrades(data);
    } catch (error) {
      console.error('Error fetching trades by instrument:', error);
    }
  };

  const fetchTradesByTransactionCode = async () => {
    try {
      const response = await fetch(`/api/trades/by-transaction-code/${transactionCode}`);
      if (!response.ok) throw new Error('Failed to fetch trades by transaction code');
      const data = await response.json();
      setTrades(data);
    } catch (error) {
      console.error('Error fetching trades by transaction code:', error);
    }
  };

  const fetchActiveTrades = async () => {
    try {
      const response = await fetch('/api/trades/active');
      if (!response.ok) throw new Error('Failed to fetch active trades');
      const data = await response.json();
      setTrades(data);
    } catch (error) {
      console.error('Error fetching active trades:', error);
    }
  };

  return (
    <div>
      <input type="file" accept=".csv" onChange={handleFileUpload} />
      
      <div>
        <input 
          type="text" 
          value={optionType} 
          onChange={(e) => setOptionType(e.target.value)} 
          placeholder="Option Type"
        />
        <button onClick={fetchTradesByOptionType}>Filter by Option Type</button>
      </div>

      <div>
        <input 
          type="text" 
          value={instrument} 
          onChange={(e) => setInstrument(e.target.value)} 
          placeholder="Instrument"
        />
        <button onClick={fetchTradesByInstrument}>Filter by Instrument</button>
      </div>

      <div>
        <input 
          type="text" 
          value={transactionCode} 
          onChange={(e) => setTransactionCode(e.target.value)} 
          placeholder="Transaction Code"
        />
        <button onClick={fetchTradesByTransactionCode}>Filter by Transaction Code</button>
      </div>

      <button onClick={fetchActiveTrades}>Show Active Trades</button>

      {trades.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Instrument</th>
              <th>Activity Date</th>
              <th>Expiry Date</th>
              <th>Option Type</th>
              <th>Strike Price</th>
              <th>Quantity</th>
              <th>Amount</th>
            </tr>
          </thead>
          <tbody>
            {trades.map((trade, index) => (
              <tr key={index}>
                <td>{trade.instrument}</td>
                <td>{trade.activityDate}</td>
                <td>{trade.quantity}</td>
                <td>{trade.amount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default TradeUploader;