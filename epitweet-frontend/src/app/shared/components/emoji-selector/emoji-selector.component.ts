import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { EmojiService, EmojiCategory, Emoji } from '../../../services/emoji.service';

@Component({
  selector: 'app-emoji-selector',
  templateUrl: './emoji-selector.component.html',
  styleUrls: ['./emoji-selector.component.scss'],
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule]
})
export class EmojiSelectorComponent implements OnInit {
  @Output() emojiSelected = new EventEmitter<string>();
  @Output() close = new EventEmitter<void>();
  
  categories: EmojiCategory[] = [];
  activeCategory: string = 'Fréquents';
  searchQuery: string = '';
  searchResults: Emoji[] = [];
  frequentlyUsed: Emoji[] = [];
  
  constructor(private emojiService: EmojiService) {}
  
  ngOnInit(): void {
    this.categories = this.emojiService.getCategories();
    this.frequentlyUsed = this.emojiService.getFrequentlyUsed();
    
    // Par défaut, montrer les emojis fréquemment utilisés
    this.activeCategory = 'Fréquents';
  }
  
  setActiveCategory(categoryName: string): void {
    this.activeCategory = categoryName;
    this.searchQuery = '';
    this.searchResults = [];
  }
  
  onSearchChange(): void {
    if (this.searchQuery.trim() === '') {
      this.searchResults = [];
      return;
    }
    
    this.searchResults = this.emojiService.searchEmojis(this.searchQuery);
    this.activeCategory = 'Recherche';
  }
  
  selectEmoji(emoji: Emoji): void {
    this.emojiSelected.emit(emoji.char);
  }
  
  closeSelector(): void {
    this.close.emit();
  }
  
  // Retourne les emojis à afficher en fonction de la catégorie active ou des résultats de recherche
  getDisplayedEmojis(): Emoji[] {
    if (this.activeCategory === 'Recherche' && this.searchQuery.trim() !== '') {
      return this.searchResults;
    } else if (this.activeCategory === 'Fréquents') {
      return this.frequentlyUsed;
    } else {
      const category = this.categories.find(cat => cat.name === this.activeCategory);
      return category ? category.emojis : [];
    }
  }
}