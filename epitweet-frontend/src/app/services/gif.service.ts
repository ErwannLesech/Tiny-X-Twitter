import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GifResult {
  id: string;
  title: string;
  url: string;
  images: {
    fixed_height: {
      url: string;
      width: string;
      height: string;
    },
    original: {
      url: string;
    }
  };
}

export interface GiphyResponse {
  data: GifResult[];
  pagination: {
    total_count: number;
    count: number;
    offset: number;
  };
  meta: {
    status: number;
    msg: string;
    response_id: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class GifService {
  // Vous devrez obtenir une clé API Giphy pour la production
  private apiKey = 'D4TBxRMHmoyTCnXgx2ysPuuLwSbBCjea'; // 100 API calls per hour
  private apiUrl = 'https://api.giphy.com/v1/gifs';
  
  constructor(private http: HttpClient) { }
  
  searchGifs(query: string, limit: number = 20): Observable<GiphyResponse> {
    const params = {
      api_key: this.apiKey,
      q: query,
      limit: limit.toString(),
      rating: 'g'
    };
    
    return this.http.get<GiphyResponse>(`${this.apiUrl}/search`, { params });
  }
  
  getTrendingGifs(limit: number = 20): Observable<GiphyResponse> {
    const params = {
      api_key: this.apiKey,
      limit: limit.toString(),
      rating: 'g'
    };
    
    return this.http.get<GiphyResponse>(`${this.apiUrl}/trending`, { params });
  }
}