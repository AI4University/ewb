export class TopicMetadata {
	id: string;
    betas: TopicBeta[];
    alphas: number;
    topic_entropy: number;
    topic_coherence: number;
    ndocs_active: number;
    tpc_descriptions: string[];
    tpc_labels: string;
    vocab: string[];
    coords: number[];
}

export class TopicBeta {
	id: string;
	beta: number;
}
