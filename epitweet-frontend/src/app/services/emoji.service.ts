import { Injectable } from '@angular/core';

export interface EmojiCategory {
  name: string;
  icon: string;
  emojis: Emoji[];
}

export interface Emoji {
  char: string;
  name: string;
  category: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmojiService {
  // Catégories d'emoji avec leurs émojis associés
  private emojiCategories: EmojiCategory[] = [
    {
      name: 'Smileys & Émotion',
      icon: '😊',
      emojis: [
        { char: '😀', name: 'Sourire', category: 'smileys' },
        { char: '😃', name: 'Sourire avec grands yeux', category: 'smileys' },
        { char: '😄', name: 'Sourire avec yeux souriants', category: 'smileys' },
        { char: '😁', name: 'Sourire rayonnant avec yeux souriants', category: 'smileys' },
        { char: '😆', name: 'Sourire avec yeux fermés', category: 'smileys' },
        { char: '😅', name: 'Sourire avec sueur', category: 'smileys' },
        { char: '🤣', name: 'Rouler par terre de rire', category: 'smileys' },
        { char: '😂', name: 'Larmes de joie', category: 'smileys' },
        { char: '🙂', name: 'Légèrement souriant', category: 'smileys' },
        { char: '🙃', name: 'À l\'envers', category: 'smileys' },
        { char: '😉', name: 'Clin d\'œil', category: 'smileys' },
        { char: '😊', name: 'Sourire avec yeux souriants', category: 'smileys' },
        { char: '😇', name: 'Sourire avec auréole', category: 'smileys' },
        { char: '🥰', name: 'Sourire avec cœurs', category: 'smileys' },
        { char: '😍', name: 'Sourire avec yeux en cœur', category: 'smileys' },
        { char: '🤩', name: 'Étoiles dans les yeux', category: 'smileys' },
        { char: '😘', name: 'Envoyer un bisou', category: 'smileys' },
        { char: '😗', name: 'Bisou', category: 'smileys' },
        { char: '☺️', name: 'Sourire', category: 'smileys' },
        { char: '😚', name: 'Bisou avec yeux fermés', category: 'smileys' },
        { char: '😙', name: 'Bisou avec yeux souriants', category: 'smileys' }
      ]
    },
    {
      name: 'Personnes & Corps',
      icon: '👋',
      emojis: [
        { char: '👋', name: 'Main qui salue', category: 'people' },
        { char: '🤚', name: 'Paume de main levée', category: 'people' },
        { char: '🖐️', name: 'Main levée avec doigts écartés', category: 'people' },
        { char: '✋', name: 'Main levée', category: 'people' },
        { char: '🖖', name: 'Salut vulcain', category: 'people' },
        { char: '👌', name: 'Signe OK', category: 'people' },
        { char: '🤌', name: 'Doigts pincés', category: 'people' },
        { char: '🤏', name: 'Main qui pince', category: 'people' },
        { char: '✌️', name: 'Victoire', category: 'people' },
        { char: '🤞', name: 'Doigts croisés', category: 'people' },
        { char: '🤟', name: 'Je t\'aime en langue des signes', category: 'people' },
        { char: '🤘', name: 'Signe des cornes', category: 'people' },
        { char: '👈', name: 'Index pointant vers la gauche', category: 'people' },
        { char: '👉', name: 'Index pointant vers la droite', category: 'people' },
        { char: '👆', name: 'Index pointant vers le haut', category: 'people' },
        { char: '👇', name: 'Index pointant vers le bas', category: 'people' },
        { char: '☝️', name: 'Index pointant vers le haut', category: 'people' },
        { char: '👍', name: 'Pouce levé', category: 'people' },
        { char: '👎', name: 'Pouce baissé', category: 'people' },
        { char: '✊', name: 'Poing levé', category: 'people' },
        { char: '👊', name: 'Poing serré', category: 'people' }
      ]
    },
    {
      name: 'Animaux & Nature',
      icon: '🐶',
      emojis: [
        { char: '🐶', name: 'Chien', category: 'animals' },
        { char: '🐱', name: 'Chat', category: 'animals' },
        { char: '🐭', name: 'Souris', category: 'animals' },
        { char: '🐹', name: 'Hamster', category: 'animals' },
        { char: '🐰', name: 'Lapin', category: 'animals' },
        { char: '🦊', name: 'Renard', category: 'animals' },
        { char: '🐻', name: 'Ours', category: 'animals' },
        { char: '🐼', name: 'Panda', category: 'animals' },
        { char: '🐨', name: 'Koala', category: 'animals' },
        { char: '🐯', name: 'Tigre', category: 'animals' },
        { char: '🦁', name: 'Lion', category: 'animals' },
        { char: '🐮', name: 'Vache', category: 'animals' },
        { char: '🐷', name: 'Cochon', category: 'animals' },
        { char: '🐸', name: 'Grenouille', category: 'animals' },
        { char: '🐵', name: 'Singe', category: 'animals' },
        { char: '🙈', name: 'Singe qui ne voit pas le mal', category: 'animals' },
        { char: '🙉', name: 'Singe qui n\'entend pas le mal', category: 'animals' },
        { char: '🙊', name: 'Singe qui ne dit pas le mal', category: 'animals' },
        { char: '🐒', name: 'Singe', category: 'animals' },
        { char: '🐔', name: 'Poule', category: 'animals' },
        { char: '🐧', name: 'Pingouin', category: 'animals' }
      ]
    },
    {
      name: 'Nourriture & Boisson',
      icon: '🍔',
      emojis: [
        { char: '🍏', name: 'Pomme verte', category: 'food' },
        { char: '🍎', name: 'Pomme rouge', category: 'food' },
        { char: '🍐', name: 'Poire', category: 'food' },
        { char: '🍊', name: 'Mandarine', category: 'food' },
        { char: '🍋', name: 'Citron', category: 'food' },
        { char: '🍌', name: 'Banane', category: 'food' },
        { char: '🍉', name: 'Pastèque', category: 'food' },
        { char: '🍇', name: 'Raisin', category: 'food' },
        { char: '🍓', name: 'Fraise', category: 'food' },
        { char: '🫐', name: 'Myrtilles', category: 'food' },
        { char: '🍈', name: 'Melon', category: 'food' },
        { char: '🍒', name: 'Cerises', category: 'food' },
        { char: '🍑', name: 'Pêche', category: 'food' },
        { char: '🥭', name: 'Mangue', category: 'food' },
        { char: '🍍', name: 'Ananas', category: 'food' },
        { char: '🥥', name: 'Noix de coco', category: 'food' },
        { char: '🥝', name: 'Kiwi', category: 'food' },
        { char: '🍅', name: 'Tomate', category: 'food' },
        { char: '🍆', name: 'Aubergine', category: 'food' },
        { char: '🥑', name: 'Avocat', category: 'food' },
        { char: '🫑', name: 'Poivron', category: 'food' }
      ]
    },
    {
      name: 'Voyage & Lieux',
      icon: '🚗',
      emojis: [
        { char: '🚗', name: 'Voiture', category: 'travel' },
        { char: '🚕', name: 'Taxi', category: 'travel' },
        { char: '🚙', name: 'SUV', category: 'travel' },
        { char: '🚌', name: 'Bus', category: 'travel' },
        { char: '🚎', name: 'Trolleybus', category: 'travel' },
        { char: '🏎️', name: 'Voiture de course', category: 'travel' },
        { char: '🚓', name: 'Voiture de police', category: 'travel' },
        { char: '🚑', name: 'Ambulance', category: 'travel' },
        { char: '🚒', name: 'Camion de pompier', category: 'travel' },
        { char: '🚐', name: 'Minibus', category: 'travel' },
        { char: '🛻', name: 'Pickup', category: 'travel' },
        { char: '🚚', name: 'Camion de livraison', category: 'travel' },
        { char: '🚛', name: 'Camion articulé', category: 'travel' },
        { char: '🚜', name: 'Tracteur', category: 'travel' },
        { char: '🛵', name: 'Scooter', category: 'travel' },
        { char: '🏍️', name: 'Moto', category: 'travel' },
        { char: '🛺', name: 'Auto-rickshaw', category: 'travel' },
        { char: '🚲', name: 'Vélo', category: 'travel' },
        { char: '🛴', name: 'Trottinette', category: 'travel' },
        { char: '🚨', name: 'Gyrophare', category: 'travel' },
        { char: '🚔', name: 'Voiture de police en approche', category: 'travel' }
      ]
    },
    {
      name: 'Symboles',
      icon: '❤️',
      emojis: [
        { char: '❤️', name: 'Cœur rouge', category: 'symbols' },
        { char: '🧡', name: 'Cœur orange', category: 'symbols' },
        { char: '💛', name: 'Cœur jaune', category: 'symbols' },
        { char: '💚', name: 'Cœur vert', category: 'symbols' },
        { char: '💙', name: 'Cœur bleu', category: 'symbols' },
        { char: '💜', name: 'Cœur violet', category: 'symbols' },
        { char: '🖤', name: 'Cœur noir', category: 'symbols' },
        { char: '🤍', name: 'Cœur blanc', category: 'symbols' },
        { char: '🤎', name: 'Cœur marron', category: 'symbols' },
        { char: '💔', name: 'Cœur brisé', category: 'symbols' },
        { char: '❣️', name: 'Point d\'exclamation en forme de cœur', category: 'symbols' },
        { char: '💕', name: 'Deux cœurs', category: 'symbols' },
        { char: '💞', name: 'Cœurs tourbillonnants', category: 'symbols' },
        { char: '💓', name: 'Cœur battant', category: 'symbols' },
        { char: '💗', name: 'Cœur grandissant', category: 'symbols' },
        { char: '💖', name: 'Cœur étincelant', category: 'symbols' },
        { char: '💘', name: 'Cœur avec flèche', category: 'symbols' },
        { char: '💝', name: 'Cœur avec ruban', category: 'symbols' },
        { char: '💟', name: 'Décoration de cœur', category: 'symbols' },
        { char: '☮️', name: 'Symbole de la paix', category: 'symbols' },
        { char: '✝️', name: 'Croix latine', category: 'symbols' }
      ]
    }
  ];

  constructor() { }

  // Retourne toutes les catégories d'emoji
  getCategories(): EmojiCategory[] {
    return this.emojiCategories;
  }

  // Retourne tous les emojis d'une catégorie donnée
  getEmojisByCategory(categoryName: string): Emoji[] {
    const category = this.emojiCategories.find(cat => cat.name === categoryName);
    return category ? category.emojis : [];
  }

  // Recherche des emojis par nom
  searchEmojis(query: string): Emoji[] {
    if (!query || query.trim() === '') {
      return [];
    }
    
    query = query.toLowerCase();
    let results: Emoji[] = [];
    
    this.emojiCategories.forEach(category => {
      const matchingEmojis = category.emojis.filter(emoji => 
        emoji.name.toLowerCase().includes(query)
      );
      results = [...results, ...matchingEmojis];
    });
    
    return results;
  }

  // Retourne les emojis fréquemment utilisés (à implémenter avec stockage local si nécessaire)
  getFrequentlyUsed(): Emoji[] {
    // Vous pourriez implémenter un système qui enregistre les emojis utilisés fréquemment
    // Pour l'instant, retournons quelques emojis populaires
    return [
      { char: '😀', name: 'Sourire', category: 'smileys' },
      { char: '👍', name: 'Pouce levé', category: 'people' },
      { char: '❤️', name: 'Cœur rouge', category: 'symbols' },
      { char: '🔥', name: 'Feu', category: 'symbols' },
      { char: '🙏', name: 'Mains en prière', category: 'people' },
      { char: '😂', name: 'Larmes de joie', category: 'smileys' },
      { char: '🤔', name: 'Pensif', category: 'smileys' },
      { char: '👀', name: 'Yeux', category: 'people' }
    ];
  }
}