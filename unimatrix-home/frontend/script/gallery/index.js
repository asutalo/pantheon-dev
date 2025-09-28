const app = Vue.createApp({
    data() {
        return {
            photos: [], // List of all photos from the JSON file
            interval: 60, // Interval in seconds (default)
            photoPool: [], // Pool of remaining photos to show
            currentPhoto: '' // Currently displayed photo
        };
    },
    methods: {
        fetchPhotos() {
            fetch('../../info/gallery/photos.json')
                .then(response => response.json())
                .then(data => {
                    this.photos = data.photos || [];
                    this.interval = data.interval || 60;

                    // Start the slideshow
                    this.initPhotoPool();
                    this.startSlideshow();
                })
                .catch(error => {
                    console.error('Error fetching photos:', error);
                });
        },
        initPhotoPool() {
            // Reset the photo pool when all images have been displayed
            this.photoPool = [...this.photos];
        },
        chooseNextPhoto() {
            // Select a random photo from the pool
            if (this.photoPool.length === 0) {
                this.initPhotoPool(); // Reset the pool if empty
            }

            const randomIndex = Math.floor(Math.random() * this.photoPool.length);
            const nextPhoto = this.photoPool[randomIndex];

            // Remove the selected photo from the pool
            this.photoPool.splice(randomIndex, 1);

            // Trigger the transition by clearing the current photo before setting the next one
            this.currentPhoto = ''; // Clear current photo
            setTimeout(() => {
                this.currentPhoto = nextPhoto; // Set the new photo after a short delay
            }, 500); // Delay matches the CSS transition duration
        },
        startSlideshow() {
            this.chooseNextPhoto(); // Choose the first photo

            setInterval(() => {
                this.chooseNextPhoto();
            }, this.interval * 1000); // Switch photos based on the defined interval
        }
    },
    mounted() {
        this.fetchPhotos();
    }
});

app.mount('#app');