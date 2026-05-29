object BloomIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return BloomNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = -1
}
