import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { GifService, GifResult } from '../../../services/gif.service'
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-gif-selector',
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule],
  templateUrl: './gif-selector.component.html',
  styleUrls: ['./gif-selector.component.scss']
})
export class GifSelectorComponent implements OnInit {
  @Output() gifSelected = new EventEmitter<string>();
  @Output() close = new EventEmitter<void>();
  
  searchQuery: string = '';
  gifs: GifResult[] = [];
  isLoading: boolean = false;
  searchSubject = new Subject<string>();
  
  constructor(private gifService: GifService) {}
  
  ngOnInit(): void {
    // Load trending GIFs initially
    this.loadTrendingGifs();
    
    // Setup debounced search
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(query => {
      this.searchGifs(query);
    });
  }
  
  onSearchChange(query: string): void {
    this.searchSubject.next(query);
  }
  
  searchGifs(query: string): void {
    if (!query.trim()) {
      this.loadTrendingGifs();
      return;
    }
    
    this.isLoading = true;
    this.gifService.searchGifs(query).subscribe({
      next: (response) => {
        this.gifs = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching GIFs:', error);
        this.isLoading = false;
      }
    });
  }
  
  loadTrendingGifs(): void {
    this.isLoading = true;
    this.gifService.getTrendingGifs().subscribe({
      next: (response) => {
        this.gifs = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading trending GIFs:', error);
        this.isLoading = false;
      }
    });
  }
  
  selectGif(gif: GifResult): void {
    this.gifSelected.emit(gif.images.original.url);
  }
  
  closeSelector(): void {
    this.close.emit();
  }
}