const app = Vue.createApp({
    data() {
        return {
            frames: [], // List of frames read from the JSON file
            currentIndex: 0 // Index of the currently displayed frame
        };
    },
    computed: {
        // Computes the current frame URL to display in the iframe
        currentFrame() {
            return this.frames[this.currentIndex] || '';
        }
    },
    methods: {
        // Navigate to the previous frame (if possible)
        goToPrevious() {
            if (this.currentIndex > 0) {
                this.currentIndex--;
            }
        },
        // Navigate to the next frame (if possible)
        goToNext() {
            if (this.currentIndex < this.frames.length - 1) {
                this.currentIndex++;
            }
        },
        // Fetch the frame list from the JSON file
        fetchFrames() {
            fetch('../../info/unimatrix-home/frames.json')
                .then(response => response.json())
                .then(data => {
                    this.frames = data.frames;
                })
                .catch(error => {
                    console.error('Error fetching frames:', error);
                });
        }
    },
    // Fetch frames when the app is mounted
    mounted() {
        this.fetchFrames();
    }
});

app.mount('#app');
